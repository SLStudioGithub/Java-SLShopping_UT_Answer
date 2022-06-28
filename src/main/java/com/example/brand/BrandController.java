package com.example.brand;

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

import com.example.entity.Brand;

@Controller
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * ブランド一覧画面表示
     *
     * @param model
     * @return ブランド一覧画面
     */
    @GetMapping
    public String listBrands(@RequestParam(required = false) String keyword, Model model) {
        // 全ブランド情報の取得
        List<Brand> listBrands = brandService.listAll(keyword);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("keyword", keyword);
        return "brands/brands";
    }

    /**
     * ブランド新規登録画面表示
     *
     * @param model
     * @return ブランド新規登録画面
     */
    @GetMapping("/new")
    public String newBrand(Model model) {
        // 新規登録用に、空のブランド情報作成
        Brand brand = new Brand();
        model.addAttribute("brand", brand);
        return "brands/brand_form";
    }

    /**
     * ブランド登録・更新処理
     *
     * @param brand ブランド情報
     * @param ra
     * @return ブランド一覧画面
     */
    @PostMapping("/save")
    public String saveBrand(Brand brand, RedirectAttributes ra) {
        // 入力値のチェック
        if (!brandService.isValid(brand)) {
            ra.addFlashAttribute("error_message", "入力に誤りがあります");
            return "redirect:/brands/new";
        }
        
        // 重複チェック
        if (!brandService.checkUnique(brand)) {
            ra.addFlashAttribute("error_message", "重複しています");
            return "redirect:/brands/new";
        }
        
        // ブランド情報の登録
        brandService.save(brand);
        // 登録成功のメッセージを格納
        ra.addFlashAttribute("success_message", "登録に成功しました");
        return "redirect:/brands";
    }

    /**
     * ブランド詳細画面表示
     *
     * @param id ブランドID
     * @param model
     * @param ra
     * @return ブランド詳細画面
     */
    @GetMapping("/detail/{id}")
    public String detailBrand(@PathVariable(name = "id") Long id, Model model, RedirectAttributes ra) {
        try {
            // ブランドIDに紐づくブランド情報取得
            Brand brand = brandService.get(id);
            model.addAttribute("brand", brand);
            return "brands/brand_detail";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("error_message", "対象のデータが見つかりませんでした");
            return "redirect:/brands";
        }
    }

    /**
     * ブランド編集画面表示
     *
     * @param id ブランドID
     * @param model
     * @param ra
     * @return ブランド編集画面
     */
    @GetMapping("/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Long id, Model model, RedirectAttributes ra) {
        try {
            // ブランドIDに紐づくブランド情報取得
            Brand brand = brandService.get(id);
            model.addAttribute("brand", brand);
            return "brands/brand_edit";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("error_message", "対象のデータが見つかりませんでした");
            return "redirect:/brands";
        }
    }

    /**
     * ブランド削除処理
     *
     * @param id ブランドID
     * @param model
     * @param ra
     * @return ブランド一覧画面
     */
    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Long id, Model model, RedirectAttributes ra) {
        // ブランド情報削除
        try {
            brandService.delete(id);
            ra.addFlashAttribute("success_message", "削除に成功しました");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("error_message", "対象のデータが見つかりませんでした");
        }
        return "redirect:/brands";
    }

}
