package com.portfoliowatch.repository;

import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Lot;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LotRepository extends JpaRepository<Lot, UUID> {

  List<Lot> findAllBySymbolAndSharesGreaterThan(Sort sort, String symbol, BigDecimal shares);

  List<Lot> findAllByAccount(Account account);

  List<Lot> findBySymbolAndAccountAndSharesGreaterThanOrderByDateTransactedAsc(
      String symbol, Account account, BigDecimal shares);

  @Query("SELECT DISTINCT symbol FROM Lot WHERE shares > 0")
  Set<String> findAllUniqueSymbols();
}
