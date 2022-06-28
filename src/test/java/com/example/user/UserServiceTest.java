package com.example.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.example.entity.User;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /** モック化したクラス */
    @Mock
    private UserRepository mockUserRepository;
    
    /** テスト対象クラスにモックを注入 */
    @InjectMocks
    private  UserService target;
    
    /**
    * テストデータの投入
    */
    @BeforeAll
    static void setUp(){
        // Oracleデータベース接続用ドライバクラスを指定
        IDatabaseConnection connection = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Oracleデータベース接続コネクションに接続URL,ユーザーID,パスワードを指定
            Connection jdbcConnection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe", "SLSHOP_UT", "slshop");
            // 返却用データベースコネクションを取得
            connection = new DatabaseConnection(jdbcConnection, "SLSHOP_UT");
            // データベースに追加するデータファイルを指定
            IDataSet iDataset = new CsvDataSet(
                    new File(System.getProperty("user.dir")
                            + "\\src\\test\\resources\\testData"));
            // データベースの指定テーブルデータを、全データ削除後に、追加するデータファイルの内容に変更
            DatabaseOperation.CLEAN_INSERT.execute(connection, iDataset);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            if (connection != null) {
                try {
                    // データベース接続用コネクションをクローズ
                    connection.close();
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }
    }
    
    /**
    * 概要 メールアドレス・管理者名のパラメーター化テスト<br>
    * 条件1 メールアドレスが10文字かつ管理者名が1文字の場合<br>
    * 条件2 メールアドレスが50文字かつ管理者名が10文字の場合<br>
    * 条件3 メールアドレスが50文字かつ管理者名が1文字の場合<br>
    * 条件4 メールアドレスが10文字かつ管理者名が10文字の場合<br>
    * 結果 trueを返すこと
    */
    @ParameterizedTest
    @CsvFileSource(resources = "paramtest_true.csv", numLinesToSkip = 1)
    void parameterTestTrue(String email, String name) {
        assertThat(target.isValid(email, name)).isTrue();
    }

    /**
    * 概要 メールアドレス・管理者名のパラメーター化テスト<br>
    * 条件1 メールアドレスが0文字かつ管理者名が0文字の場合<br>
    * 条件2 メールアドレスが51文字かつ管理者名が11文字の場合<br>
    * 条件3 メールアドレスが50文字かつ管理者名が0文字の場合<br>
    * 条件4 メールアドレスが0文字かつ管理者名が10文字の場合<br>
    * 条件5 メールアドレスが50文字かつ管理者名が11文字の場合<br>
    * 条件6 メールアドレスが51文字かつ管理者名が10文字の場合<br>
    * 結果 falseを返すこと
    */
    @ParameterizedTest
    @CsvFileSource(resources = "paramtest_false.csv", numLinesToSkip = 1)
    void parameterTestFalse(String email, String name) {
        assertThat(target.isValid(email, name)).isFalse();
    }   
    
    /**
    * 概要 管理者メールアドレスの重複チェック<br>
    * 条件 管理者メールアドレスが重複していない場合<br>
    * 結果 trueを返すこと
    */
    @Test
    void 管理者メールアドレスが重複していない場合trueを返すこと() {
        User newUser= new User("abc@example.com");
        
        doReturn(null).when(this.mockUserRepository).findByEmail(anyString());
        
        assertThat(target.checkUnique(newUser)).isTrue();
    }
    /** 簡易版 */
    // @Test
    // void 管理者メールアドレスが重複していない場合trueを返すこと() {
    //     User user = new User("abc@example.com");
    //     assertThat(target.checkUnique(user)).isTrue();
    // }
    
    /**
    * 概要 管理者メールアドレスの重複チェック<br>
    * 条件 管理者メールアドレスが重複する場合<br>
    * 結果 falseを返すこと
    */
    @Test
    void 管理者メールアドレスが重複する場合falseを返すこと() {
        User newUser = new User("admin@example.com");
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("admin@example.com");
        
        doReturn(mockUser).when(this.mockUserRepository).findByEmail(newUser.getEmail());
        
        assertThat(target.checkUnique(newUser)).isFalse();
    }
    /** 簡易版 */
    // @Test
    // void 管理者メールアドレスが重複する場合falseを返すこと() {
    //     User user = new User("admin@example.com");
    //     assertThat(target.checkUnique(user)).isFalse();
    // }
    
    /**
    * 概要 管理者情報の取得<br>
    * 条件 指定した管理者IDに対応する管理者情報が存在する場合<br>
    * 結果 例外が発生しないこと
    */
    @Test
    void 管理者情報が存在する場合例外が発生しないこと() {
        Long id = 1L;
        
        Long count = 1L;
        Optional<User> user = Optional.of(new User());

        doReturn(count).when(this.mockUserRepository).countById(id);
        doReturn(user).when(this.mockUserRepository).findById(id);

        assertThatCode(() -> {
            target.get(id);
        }).doesNotThrowAnyException();
    }
    /** 簡易版 */
    // @Test
    // void 管理者情報が存在する場合例外が発生しないこと() {
    //     assertThatCode(() -> {
    //         target.get(1L);
    //     }).doesNotThrowAnyException();
    // }

    /**
    * 概要 管理者情報の取得<br>
    * 条件 指定した管理者IDに対応する管理者情報が存在しない場合<br>
    * 結果 例外が発生すること
    */
    @Test
    void 管理者情報が存在しない場合例外が発生すること() {
        Long id = 1000L;

        doReturn(null).when(this.mockUserRepository).countById(id);
        
        assertThatThrownBy(() -> {
            target.get(id);
        })
        .isInstanceOf(NotFoundException.class);
    }
    /** 簡易版 */
    // @Test
    // void 管理者情報が存在しない場合例外が発生すること() {
    //     assertThatThrownBy(() -> {
    //         target.get(1000L);
    //     })
    //     .isInstanceOf(NotFoundException.class);
    // }
    
    /**
    * 管理者情報の取得処理の検証<br>
    * 条件 空の管理者情報をスタブに設定する<br>
    * 結果 取得結果がスタブで設定した管理者情報と等しいこと
    */
    @Test
    void 管理者情報の取得処理の検証() throws Exception {
        Long id = 1L;
        
        Long count = 1L;
        Optional<User> user = Optional.of(new User());

        doReturn(count).when(this.mockUserRepository).countById(id);
        doReturn(user).when(this.mockUserRepository).findById(id);

        User actual = this.target.get(id);
        assertThat(actual).isEqualTo(user.get());
        
    }
}
