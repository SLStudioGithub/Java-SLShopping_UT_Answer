package com.example.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.example.entity.Category;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * カテゴリー情報全件取得
     *
     * @return カテゴリー情報のリスト
     */
    public List<Category> listAll() {
        return categoryRepository.findAll();
    }

    /**
     * カテゴリー情報検索処理
     *
     * @param keyword 検索キーワード
     * @return カテゴリー情報のリスト
     */
    public List<Category> listAll(String keyword) {
        // 検索キーワードがあった場合
        if (keyword != null && !keyword.isEmpty()) {
            return categoryRepository.search(keyword);
        }
        // それ以外の場合
        else {
            return categoryRepository.findAll();
        }
    }

    /**
     * IDに紐づくカテゴリー情報取得処理
     *
     * @param id カテゴリーID
     * @return カテゴリー情報
     * @throws NotFoundException 
     */
    public Category get(Long id) throws NotFoundException {
        // IDに紐づくカテゴリー情報が存在するかの確認
        if (!this.exists(id)) {
            throw new NotFoundException();
        }
        return categoryRepository.findById(id).get();
    }

    /**
     * カテゴリー情報登録処理
     *
     * @param category 保存したいカテゴリー情報
     * @return 保存したカテゴリー情報
     */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * カテゴリー情報の入力値チェック
     *
     * @param brand 保存したいカテゴリー情報
     * @return true:正常の入力値 false:異常な入力値
     */
    public boolean isValid(Category category) {
        // 保存したいカテゴリー名の文字数を取得
        int nameLength = category.getName().length();
        
        // 文字数の判定（1文字から32文字まで）
        if (nameLength < 1 || nameLength > 32) {
            return false;
        }
        return true;
    }
 
    /**
     * カテゴリー名の重複チェック
     *
     * @param name 重複確認したいブランド情報
     * @return true:重複なし false:重複あり
     */
    public boolean checkUnique(Category category) {
        boolean isCreatingNew = (category.getId() == null || category.getId() == 0);
        Category categoryByName = categoryRepository.findByName(category.getName());

        if (isCreatingNew) {
            if (categoryByName != null) {
                return false;
            }
        } else {
            if (categoryByName != null && categoryByName.getId() != category.getId()) {
                return false;
            }
        }
        return true;
    }

    /**
     * IDに紐づくカテゴリー情報削除処理
     *
     * @param id カテゴリーID
     * @throws NotFoundException 
     */
    public void delete(Long id) throws NotFoundException {
        // IDに紐づくカテゴリー情報が存在するかの確認
        if (!this.exists(id)) {
            throw new NotFoundException();
        }
        categoryRepository.deleteById(id);
    }

    /**
     * カテゴリー情報の存在チェック
     *
     * @param name 確認したいカテゴリー情報のID
     * @return true:存在する false:存在しない
     */
    private boolean exists(Long id) {
        Long countById = categoryRepository.countById(id);
        if (countById == null || countById == 0L) {
            return false;
        }
        return true;
    }
   
}
