package com.portfoliowatch.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "watched_symbols")
public class WatchedSymbol {

    @Id
    @Column(nullable = false, length = 5)
    private String symbol;

}