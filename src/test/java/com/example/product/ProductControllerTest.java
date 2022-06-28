package com.example.product;

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

import com.example.brand.BrandService;
import com.example.category.CategoryService;
import com.example.entity.Product;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    /** モック化したクラス */
    @Mock
    private ProductService mockProductService;

    @Mock
    private BrandService mockBrandService;

    @Mock
    private CategoryService mockCategoryService;

    /*
     * 実務シミュではstaticメソッドを利用するようにしていたが、staticメソッドのmock化が高難易度のため、
     * DI利用した実装に変更している
     */
    @Mock
    private ProductSaveHelper productSaveHelper;

    /** テスト対象クラスにモックを注入 */
    @InjectMocks
    private ProductController target;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // MockMvcの生成
        this.mockMvc = MockMvcBuilders.standaloneSetup(target).alwaysDo(log()).build();
    }

    /**
     * 商品一覧表示画面の検証
     */
    @Test
    void listProductsTest() throws Exception {
        List<Product> products = new ArrayList<>();
        String keyword = null;

        doReturn(products).when(this.mockProductService).listAll(keyword);

        this.mockMvc.perform(get("/products").param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(view().name("products/products"))
                .andExpect(model().attribute("listProducts", products))
                .andExpect(model().attribute("keyword", keyword));

    }

    /**
     * 商品新規登録画面の検証
     */
    @Test
    void newProductTest() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product_form")).andReturn();

        Product actual = (Product) result.getModelAndView().getModel().get("product");
        assertThat(actual).isInstanceOf(Product.class);
    }

    /**
     * 商品登録・更新処理の検証
     */
    @Test
    void saveProductTest() throws Exception {
        Product product = new Product("testName", "testDescription");

        doReturn(true).when(this.mockProductService).isValid(product.getName(), product.getDescription());
        doReturn(true).when(this.mockProductService).checkUnique(product);
        doReturn(null).when(this.mockProductService).save(product);

        this.mockMvc.perform(post("/products/save").flashAttr("product", product))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attribute("success_message", "登録に成功しました"));

    }

    /**
     * 商品詳細画面の検証
     */
    @Test
    void detailProductTest() throws Exception {
        Long id = 1L;
        Product product = new Product();

        doReturn(product).when(this.mockProductService).get(id);

        this.mockMvc.perform(get("/products/detail/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product_detail"))
                .andExpect(model().attribute("product", product));
    }

    /**
     * 商品編集画面の検証
     */
    @Test
    void editProductTest() throws Exception {
        Long id = 1L;
        Product product = new Product();

        when(this.mockProductService.get(id)).thenReturn(product);

        this.mockMvc.perform(get("/products/edit/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product_edit"))
                .andExpect(model().attribute("product", product));

    }

    /**
     * 商品削除の検証
     */
    @Test
    void deleteProductTest() throws Exception {
        Long id = 1L;

        doNothing().when(this.mockProductService).delete(id);

        this.mockMvc.perform(get("/products/delete/{id}", id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attribute("success_message", "削除に成功しました"));
    }
}
