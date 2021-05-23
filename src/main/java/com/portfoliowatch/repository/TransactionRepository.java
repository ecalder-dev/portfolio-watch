package com.portfoliowatch.repository;

import com.portfoliowatch.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t " +
            "ORDER by t.dateTransacted ASC, t.executionPriority ASC, " +
            "t.price ASC, t.datetimeInserted ASC")
    List<Transaction> findAllOrdered();

}