package com.example.brand;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.entity.Brand;

@ExtendWith(MockitoExtension.class) // JUnit5でMockito使うために書く
class BrandControllerTest {

    /** モック化したクラス */
    @Mock
    private BrandService mockBrandService;

    /** テスト対象クラスにモックを注入 */
    @InjectMocks
    private BrandController target;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // MockMvcの生成
        this.mockMvc = MockMvcBuilders.standaloneSetup(target).alwaysDo(log()).build();
    }

    /**
     * ブランド一覧表示画面の検証
     */
    @Test
    void listBrandsTest() throws Exception {
        // 準備
        List<Brand> brands = new ArrayList<>();
        String keyword = null;

        // スタブを設定
        // doReturn(返り値の設定).when(対象のモック).対象のメソッド(引数)
        doReturn(brands).when(this.mockBrandService).listAll(keyword);
        
        // 検証
        /*
         * perform(get("path") httpメソッドとパスの指定
         * param("key", val) クエリストリングの指定
         * andExpect(検証したいこと)
         * 検証例：
         * status().isOk() ステータスコードの検証
         * view().name("テンプレートファイル名") テンプレートファイルの呼び出しがあっているか
         * model().attribute("key", val) modelに格納されているか
         */
        this.mockMvc.perform(get("/brands").param("keyword", keyword)) // リクエストの情報
                .andExpect(status().isOk()) // ステータスの検証
                .andExpect(view().name("brands/brands")) // テンプレートファイルの呼び出し検証
                .andExpect(model().attribute("listBrands", brands)) // modelに格納されている要素の検証
                .andExpect(model().attribute("keyword", keyword));

    }

    /**
     * ブランド新規登録画面の検証
     */
    @Test
    void newBrandTest() throws Exception {
        // 検証
        MvcResult result = this.mockMvc.perform(get("/brands/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand_form")).andReturn();

        // 検証 modelにブランド情報が格納されているかを検証
        Brand actual = (Brand) result.getModelAndView().getModel().get("brand");
        assertThat(actual).isInstanceOf(Brand.class);
    }

    /**
     * ブランド登録・更新処理の検証
     */
    @Test
    void saveBrandTest() throws Exception {
        // 準備
        Brand brand = new Brand();

        // スタブを設定
        doReturn(true).when(this.mockBrandService).isValid(brand);
        doReturn(true).when(this.mockBrandService).checkUnique(brand);
        doReturn(null).when(this.mockBrandService).save(brand);
     
        // 検証
        /*
         * andExpect(検証したいこと) 新登場
         * 検証例：
         * redirectedUrl("path") リダイレクト先の検証
         * flash().attribute("key", val) // リダイレクト時の引継ぎ情報の検証
         */
        this.mockMvc.perform(post("/brands/save").flashAttr("brand", brand))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/brands"))
                .andExpect(flash().attribute("success_message", "登録に成功しました"));

    }

    /**
     * ブランド詳細画面の検証
     */
    @Test
    void detailBrandTest() throws Exception {
        // 準備
        Long id = 1L;
        Brand brand = new Brand();

        // スタブを設定
        doReturn(brand).when(this.mockBrandService).get(id);

        // 検証
        this.mockMvc.perform(get("/brands/detail/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand_detail"))
                .andExpect(model().attribute("brand", brand));
    }

    /**
     * ブランド編集画面の検証
     */
    @Test
    void editBrandTest() throws Exception {
        // 準備
        Long id = 1L;
        Brand brand = new Brand();
        
        // スタブを設定
        when(this.mockBrandService.get(id)).thenReturn(brand);
        
        // 検証
        this.mockMvc.perform(get("/brands/edit/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand_edit"))
                .andExpect(model().attribute("brand", brand));

    }

    /**
     * ブランド削除の検証
     */
    @Test
    void deleteBrandTest() throws Exception {
        // 準備
        Long id = 1L;

        // スタブの設定 doNothing()は返り値がないとき
        doNothing().when(this.mockBrandService).delete(id);

        // 検証
        this.mockMvc.perform(get("/brands/delete/{id}", id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/brands"))
                .andExpect(flash().attribute("success_message", "削除に成功しました"));

    }
}
