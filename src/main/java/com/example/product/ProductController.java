package com.example.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.brand.BrandService;
import com.example.category.CategoryService;
import com.example.entity.Brand;
import com.example.entity.Category;
import com.example.entity.Product;


@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    private final BrandService brandService;

    private final CategoryService categoryService;

    private final ProductSaveHelper productSaveHelper;
    
    @Autowired
    public ProductController(
            ProductService productService,
            BrandService brandService,
            CategoryService categoryService,
            ProductSaveHelper productSaveHelper) {
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.productSaveHelper = productSaveHelper;
    }

    /**
     * 商品一覧画面表示
     *
     * @param model
     * @return 商品一覧画面
     */
    @GetMapping
    public String listProducts(@RequestParam(required = false) String keyword, Model model) {
        // 全商品情報の取得
        List<Product> listProducts = productService.listAll(keyword);
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("keyword", keyword);
        return "products/products";
    }

    /**
     * 商品新規登録画面表示
     *
     * @param model
     * @return 商品新規登録画面
     */
    @GetMapping("/new")
    public String newProduct(Model model) {
        // 新規登録用に、空の商品情報作成
        Product product = new Product();
        // 全ブランド情報の取得
        List<Brand> listBrands = brandService.listAll();
        // 全カテゴリー情報の取得
        List<Category> listCategories = categoryService.listAll();
        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("listCategories", listCategories);
        return "products/product_form";
    }

    /**
     * 商品登録・更新処理
     *
     * @param product 商品情報
     * @param file 商品画像
     * @param redirectAttributes
     * @return 商品一覧画面
     */
    @PostMapping("/save")
    public String saveProduct(Product product, MultipartFile file, RedirectAttributes ra) throws IOException {
        // 入力値のチェック
        if (!productService.isValid(product.getName(), product.getDescription())) {
            ra.addFlashAttribute("error_message", "入力に誤りがあります");
            return "redirect:/products/new";
        }

        // 重複チェック
        if (!productService.checkUnique(product)) {
            ra.addFlashAttribute("error_message", "重複しています");
            return "redirect:/products/new";
        }
                
        // 商品画像のファイル名を取得し、商品情報に格納する
        productSaveHelper.setMainImageName(file, product);
        // 商品情報の登録
        Product savedProduct = productService.save(product);
        // 商品画像のファイルを保存する
        productSaveHelper.saveUploadedImages(file, savedProduct);
        ra.addFlashAttribute("success_message", "登録に成功しました");
        return "redirect:/products";
    }

    /**
     * 商品詳細画面表示
     *
     * @param id 商品ID
     * @param model
     * @param ra 
     * @return 商品詳細画面
     */
    @GetMapping("/detail/{id}")
    public String detailUser(@PathVariable(name = "id") Long id, Model model, RedirectAttributes ra) {
        try {
            // 商品IDに紐づく商品情報取得
            Product product = productService.get(id);
            model.addAttribute("product", product);
            return "products/product_detail";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("error_message", "対象のデータが見つかりませんでした");
            return "redirect:/products";
        }

    }

    /**
     * 商品編集画面表示
     *
     * @param id 商品ID
     * @param model
     * @param ra 
     * @return 商品編集画面
     */
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            // 商品IDに紐づく商品情報取得
            Product product = productService.get(id);
            model.addAttribute("product", product);
        } catch (NotFoundException e) {
            ra.addFlashAttribute("error_message", "対象のデータが見つかりませんでした");
            return "redirect:/products";            
        }
        // 全ブランド情報の取得
        List<Brand> listBrands = brandService.listAll();
        // 全カテゴリー情報の取得
        List<Category> listCategories = categoryService.listAll();

        model.addAttribute("listBrands", listBrands);
        model.addAttribute("listCategories", listCategories);

        return "products/product_edit";
    }

    /**
     * 商品削除処理
     *
     * @param id 商品ID
     * @param model
     * @param ra
     * @return 商品一覧画面
     */
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Long id, Model model, RedirectAttributes ra) {
        try {
            // 商品情報削除
            productService.delete(id);
            ra.addFlashAttribute("success_message", "削除に成功しました");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("error_message", "対象のデータが見つかりませんでした");
        }
        return "redirect:/products";
    }

}
