package com.example.category;

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

import com.example.entity.Category;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    /** モック化したクラス */
    @Mock
    private CategoryService mockCategoryService;

    /** テスト対象クラスにモックを注入 */
    @InjectMocks
    private CategoryController target;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // MockMvcの生成
        this.mockMvc = MockMvcBuilders.standaloneSetup(target).alwaysDo(log()).build();
    }

    /**
     * カテゴリー一覧表示画面の検証
     */
    @Test
    void listCategoriesTest() throws Exception {
        List<Category> categories = new ArrayList<>();
        String keyword = null;

        doReturn(categories).when(this.mockCategoryService).listAll(keyword);
        
        this.mockMvc.perform(get("/categories").param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/categories"))
                .andExpect(model().attribute("listCategories", categories))
                .andExpect(model().attribute("keyword", keyword));
    }

    /**
     * カテゴリー新規登録画面の検証
     */
    @Test
    void newCategoryTest() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/categories/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/category_form")).andReturn();
        
        Category actual = (Category) result.getModelAndView().getModel().get("category");
        assertThat(actual).isInstanceOf(Category.class);
    }

    /**
     * カテゴリー登録・更新処理の検証
     */
    @Test
    void saveCategoryTest() throws Exception {
        Category category = new Category();

        doReturn(true).when(this.mockCategoryService).isValid(category);
        doReturn(true).when(this.mockCategoryService).checkUnique(category);
        doReturn(null).when(this.mockCategoryService).save(category);
     
        this.mockMvc.perform(post("/categories/save").flashAttr("category", category))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/categories"))
                .andExpect(flash().attribute("success_message", "登録に成功しました"));

    }

    /**
     */
    @Test
    void detailCategoryTest() throws Exception {
        Long id = 1L;
        Category category = new Category();

        doReturn(category).when(this.mockCategoryService).get(id);

        this.mockMvc.perform(get("/categories/detail/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/category_detail"))
                .andExpect(model().attribute("category", category));
    }

    /**
     * カテゴリー編集画面の検証
     */
    @Test
    void editCategoryTest() throws Exception {
        Long id = 1L;
        Category category = new Category();
        
        when(this.mockCategoryService.get(id)).thenReturn(category);
        this.mockMvc.perform(get("/categories/edit/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/category_edit"))
                .andExpect(model().attribute("category", category));

    }

    /**
     * カテゴリー削除の検証
     */
    @Test
    void deleteCategoryTest() throws Exception {
        Long id = 1L;

        doNothing().when(this.mockCategoryService).delete(id);

        this.mockMvc.perform(get("/categories/delete/{id}", id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/categories"))
                .andExpect(flash().attribute("success_message", "削除に成功しました"));

    }
}
