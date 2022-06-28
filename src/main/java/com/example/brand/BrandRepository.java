package com.example.brand;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    /**
     * ブランド情報の件数取得
     *
     * @param id ブランドID
     * @return 取得件数
     */
    public Long countById(Long id);

    /**
     * ブランド情報検索クエリ
     *
     * @param name ブランド名
     * @return ブランド情報
     */
    public Brand findByName(String name);

    /**
     * ブランド情報検索クエリ
     *
     * @param keyword 検索キーワード
     * @return ブランド情報のリスト
     */
    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    public List<Brand> search(String keyword);

}
