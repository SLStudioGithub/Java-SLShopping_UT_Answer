package com.example.user;

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

import com.example.entity.User;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    /** モック化したクラス */
    @Mock
    private UserService mockUserService;

    /** テスト対象クラスにモックを注入 */
    @InjectMocks
    private UserController target;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // MockMvcの生成
        this.mockMvc = MockMvcBuilders.standaloneSetup(target).alwaysDo(log()).build();
    }

    /**
     * 管理者一覧表示画面の検証
     */
    @Test
    void listUsersTest() throws Exception {
        List<User> users = new ArrayList<>();
        String keyword = null;

        doReturn(users).when(this.mockUserService).listAll(keyword);
        
        this.mockMvc.perform(get("/users").param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(view().name("users/users"))
                .andExpect(model().attribute("listUsers", users))
                .andExpect(model().attribute("keyword", keyword));

    }

    /**
     * 管理者新規登録画面の検証
     */
    @Test
    void newUserTest() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/user_form")).andReturn();

        User actual = (User) result.getModelAndView().getModel().get("user");
        assertThat(actual).isInstanceOf(User.class);
    }

    /**
     * 管理者登録・更新処理の検証
     */
    @Test
    void saveUserTest() throws Exception {
        User user = new User("testEmail", "testName");

        doReturn(true).when(this.mockUserService).isValid(user.getEmail(), user.getName());
        doReturn(true).when(this.mockUserService).checkUnique(user);
        doReturn(null).when(this.mockUserService).save(user);
     
        this.mockMvc.perform(post("/users/save").flashAttr("user", user))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attribute("success_message", "登録に成功しました"));

    }

    /**
     * 管理者詳細画面の検証
     */
    @Test
    void detailUserTest() throws Exception {
        Long id = 1L;
        User user = new User();

        doReturn(user).when(this.mockUserService).get(id);

        this.mockMvc.perform(get("/users/detail/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("users/user_detail"))
                .andExpect(model().attribute("user", user));
    }

    /**
     * 管理者編集画面の検証
     */
    @Test
    void editUserTest() throws Exception {
        Long id = 1L;
        User user = new User();
        
        when(this.mockUserService.get(id)).thenReturn(user);
        
        this.mockMvc.perform(get("/users/edit/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("users/user_edit"))
                .andExpect(model().attribute("user", user));

    }

    /**
     * 管理者削除の検証
     * 
     * Spring Securityと絡んだテストが必要となり、かなり複雑になるため、
     * 実務シミュの実装からSpring Securityを使った実装部分を削除している
     */
    @Test
    void deleteUserTest() throws Exception {
        Long id = 1L;

        doNothing().when(this.mockUserService).delete(id);

        this.mockMvc.perform(get("/users/delete/{id}", id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attribute("success_message", "削除に成功しました"));

    }
}
