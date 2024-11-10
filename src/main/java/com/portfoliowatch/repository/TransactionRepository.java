package com.portfoliowatch.repository;

import com.portfoliowatch.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t " +
            "ORDER by t.dateTransacted ASC, " +
            "t.datetimeCreated ASC")
    List<Transaction> findAllOrdered();

}