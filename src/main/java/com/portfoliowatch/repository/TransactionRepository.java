package com.portfoliowatch.repository;

import com.portfoliowatch.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.account.accountId = ?1 " +
            "ORDER by t.dateTransacted ASC, t.price ASC, " +
            "t.executionPriority ASC, t.transactionId ASC")
    List<Transaction> findAllByAccountId(Long accountId);

}