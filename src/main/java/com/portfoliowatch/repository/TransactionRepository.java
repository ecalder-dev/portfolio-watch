package com.portfoliowatch.repository;

import com.portfoliowatch.model.entity.Transaction;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  @Query(
      "SELECT t FROM Transaction t " + "ORDER by t.dateTransacted ASC, " + "t.datetimeCreated ASC")
  List<Transaction> findAllOrdered();

  @Query("SELECT MAX(t.datetimeUpdated) FROM Transaction t")
  Date findLatestDatetimeUpdated();
}
