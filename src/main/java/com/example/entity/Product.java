package com.example.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 商品情報
 */
@Entity
@Table(name = "PRODUCTS")
public class Product {

    @Id
    @SequenceGenerator(name = "PRODUCTS_ID_GENERATOR", sequenceName = "PRODUCTS_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUCTS_ID_GENERATOR")
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", unique = true, length = 10, nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", length = 50, nullable = false)
    private String description;

    @Column(name = "PRICE")
    private int price;

    @Column(name = "IMAGE")
    private String image;

    @Column(name = "LENGTH")
    private double length;

    @Column(name = "WIDTH")
    private double width;

    @Column(name = "HEIGHT")
    private double height;

    @Column(name = "WEIGHT")
    private double weight;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "BRAND_ID")
    private Brand brand;

    public Product() {
    }

    public Product(String name) {
        this.name = name;
    }
    
    public Product(String name, String description) {
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getImage() {
        return "/product-images/" + id + "/" + image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
