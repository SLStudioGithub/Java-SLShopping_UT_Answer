package com.example.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * ブランド情報
 */
@Entity
@Table(name = "BRANDS")
public class Brand {

    @Id
    @SequenceGenerator(name = "BRANDS_ID_GENERATOR", sequenceName = "BRANDS_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BRANDS_ID_GENERATOR")
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false, length = 10, unique = true)
    private String name;

    
    public Brand() {
    }
    
    public Brand(String name) {
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
