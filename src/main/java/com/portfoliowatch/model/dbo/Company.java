package com.portfoliowatch.model.dbo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString
@Table(name = "company")
public class Company {

    @Id
    @Column(nullable = false, length = 5)
    private String symbol;

    @Column(length = 100)
    private String name;

    @Lob
    @Column
    private String description;

    @Column(length = 100)
    private String address;

    @Column(length = 100)
    private String url;

    @Column(length = 100)
    private String industry;

    @Column(length = 50)
    private String sector;
}
