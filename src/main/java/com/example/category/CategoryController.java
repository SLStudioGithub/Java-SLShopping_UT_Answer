package com.example.category;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.Category;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * カテゴリー一覧画面表示
     *
     * @param model
     * @return カテゴリー一覧画面
     */
    @GetMapping
    public String listCategories(@RequestParam(required = false) String keyword, Model model) {
        // 全カテゴリー情報の取得
        List<Category> listCategories = categoryService.listAll(keyword);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("keyword", keyword);
        return "categories/categories";
    }

    /**
     * カテゴリー新規登録画面表示
     *
     * @param model
     * @return カテゴリー新規登録画面
     */
    @GetMapping("/new")
    public String newCategory(Model model) {
        // 新規登録用に、空のカテゴリー情報作成
        Category category = new Category();
        model.addAttribute("category", category);
        return "categories/category_form";
    }

    /**
     * カテゴリー登録・更新処理
     *
     * @param category カテゴリー情報
     * @param ra
     * @return カテゴリー一覧画面
     */
    @PostMapping("/save")
    public String saveCategory(Category category, RedirectAttributes ra) {
        // 入力値のチェック
        if (!categoryService.isValid(category)) {
            ra.addFlashAttribute("error_message", "入力に誤りがあります");
            return "redirect:/brands/new";
        }
        
        // 重複チェック
        if (!categoryService.checkUnique(category)) {
            ra.addFlashAttribute("error_message", "重複しています");
            return "redirect:/brands/new";
        }
        
        // カテゴリー情報の登録
        categoryService.save(category);
        // 登録成功のメッセージを格納
        ra.addFlashAttribute("success_message", "登録に成功しました");
        return "redirect:/categories";
    }

    /**
     * カテゴリー詳細画面表示
     *
     * @param id カテゴリーID
     * @param model
     * @param ra 
     * @return カテゴリー詳細画面
     */
    @GetMapping("/detail/{id}")
    public String detailCategory(@PathVariable(name = "id") Long id, Model model, RedirectAttributes ra) {
        try {
            // カテゴリーIDに紐づくカテゴリー情報取得
            Category category = categoryService.get(id);
            model.addAttribute("category", category);
            return "categories/category_detail";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("error_message", "対象のデータが見つかりませんでした");
            return "redirect:/categories";
        }
    }

    /**
     * カテゴリー編集画面表示
     *
     * @param id カテゴリーID
     * @param model
     * @param ra 
     * @return カテゴリー編集画面
     */
    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Long id, Model model, RedirectAttributes ra) {
        try {
            // カテゴリーIDに紐づくカテゴリー情報取得
            Category category = categoryService.get(id);
            model.addAttribute("category", category);
            return "categories/category_edit";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("error_message", "対象のデータが見つかりませんでした");
            return "redirect:/categories";
        }
    }

    /**
     * カテゴリー削除処理
     *
     * @param id カテゴリーID
     * @param model
     * @param ra
     * @return カテゴリー一覧画面
     */
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Long id, Model model, RedirectAttributes ra) {
        // カテゴリー情報削除
        try {
            categoryService.delete(id);
            ra.addFlashAttribute("success_message", "削除に成功しました");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("error_message", "対象のデータが見つかりませんでした");
        }
        return "redirect:/categories";
    }

}
