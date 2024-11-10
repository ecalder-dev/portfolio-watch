package com.portfoliowatch.repository;

import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Lot;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {

    List<Lot> findAllBySymbol(Sort sort, String symbol);

    List<Lot> findAllByAccount(Account account);

    List<Lot> findAllBySymbolAndAccount(Sort sort, String symbol, Account account);

    @Query("SELECT DISTINCT symbol FROM Lot")
    Set<String> findAllUniqueSymbols();
}