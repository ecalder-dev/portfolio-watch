package com.portfoliowatch.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter @Setter
@Entity
@Table(name = "watched_symbols")
public class WatchedSymbol {

    @Id
    @Column(nullable = false, length = 5)
    private String symbol;

}