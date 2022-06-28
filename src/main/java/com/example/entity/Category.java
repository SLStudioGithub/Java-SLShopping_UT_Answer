package com.example.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * カテゴリー情報
 */
@Entity
@Table(name = "CATEGORIES")
public class Category {

    @Id
    @SequenceGenerator(name = "CATEGORIES_ID_GENERATOR", sequenceName = "CATEGORIES_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORIES_ID_GENERATOR")
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", length = 32, nullable = false, unique = true)
    private String name;

    public Category() {
    }
    
    public Category(String name) {
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
