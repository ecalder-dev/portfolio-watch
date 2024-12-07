package com.portfoliowatch.model.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Data
@Entity
@Table(name = "company")
public class Company {

    @Id
    @Column(nullable = false, length = 5)
    private String symbol;

    @Column(length = 100)
    private String name;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(length = 300)
    private String address;

    @Column(length = 100)
    private String url;

    @Column(length = 100)
    private String industry;

    @Column(length = 50)
    private String sector;
}
