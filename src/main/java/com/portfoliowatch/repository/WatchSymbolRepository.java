package com.portfoliowatch.repository;

import com.portfoliowatch.model.WatchedSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchSymbolRepository extends JpaRepository<WatchedSymbol, String> {



}