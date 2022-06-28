package com.example.brand;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.example.entity.Brand;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    /**
     * ブランド情報全件取得
     *
     * @return ブランド情報のリスト
     */
    public List<Brand> listAll() {
        return brandRepository.findAll();
    }

    /**
     * ブランド情報検索処理
     *
     * @param keyword 検索キーワード
     * @return ブランド情報のリスト
     */
    public List<Brand> listAll(String keyword) {
        // 検索キーワードがあった場合
        if (keyword != null && !keyword.isEmpty()) {
            return brandRepository.search(keyword);
        }
        // それ以外の場合
        else {
            return brandRepository.findAll();
        }
    }

    /**
     * IDに紐づくブランド情報取得処理
     *
     * @param id ブランドID
     * @return ブランド情報
     * @throws NotFoundException 
     */
    public Brand get(Long id) throws NotFoundException {
        // IDに紐づくブランド情報が存在するかの確認
        if (!this.exists(id)) {
            throw new NotFoundException();
        }
        return brandRepository.findById(id).get();
    }

    /**
     * ブランド情報登録処理
     *
     * @param brand 保存したいブランド情報
     * @return 保存したブランド情報
     */
    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    /**
     * ブランド情報の入力値チェック
     *
     * @param brand 保存したいブランド情報
     * @return true:正常の入力値 false:異常な入力値
     */
    public boolean isValid(Brand brand) {
        // 保存したいブランド名の文字数を取得
        int nameLength = brand.getName().length();
        
        // 文字数の判定（1文字から10文字まで）
        if (nameLength < 1 || nameLength > 10) {
            return false;
        }
        return true;
    }
 
    /**
     * ブランド名の重複チェック
     *
     * @param name 重複確認したいブランド情報
     * @return true:重複なし false:重複あり
     */
    public boolean checkUnique(Brand brand) {
        boolean isCreatingNew = (brand.getId() == null || brand.getId() == 0);
        Brand brandByName = brandRepository.findByName(brand.getName());

        if (isCreatingNew) {
            if (brandByName != null) {
                return false;
            }
        } else {
            if (brandByName != null && brandByName.getId() != brand.getId()) {
                return false;
            }
        }
        return true;
    }

    /**
     * IDに紐づくブランド情報削除処理
     *
     * @param id ブランドID
     * @throws NotFoundException 
     */
    public void delete(Long id) throws NotFoundException {
        // IDに紐づくブランド情報が存在するかの確認
        if (!this.exists(id)) {
            throw new NotFoundException();
        }
        brandRepository.deleteById(id);
    }

    /**
     * ブランド情報の存在チェック
     *
     * @param name 確認したいブランド情報のID
     * @return true:存在する false:存在しない
     */
    private boolean exists(Long id) {
        Long countById = brandRepository.countById(id);
        if (countById == null || countById == 0L) {
            return false;
        }
        return true;
    }
    
}
