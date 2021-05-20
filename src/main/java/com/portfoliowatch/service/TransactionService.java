package com.portfoliowatch.service;

import com.portfoliowatch.model.Transaction;
import com.portfoliowatch.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    public List<Transaction> readAllTransactions(Sort sort) {
        if (sort != null) {
            return transactionRepository.findAll(sort);
        } else {
            return transactionRepository.findAll();
        }
    }

    public Transaction readTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public Transaction createTransaction(Transaction transaction) {
        transaction.setSymbol(transaction.getSymbol().toUpperCase());
        transaction.setTransactionId(null);
        transaction.setDatetimeUpdated(new Date());
        transaction.setDatetimeInserted(new Date());
        this.setExecutionPriority(transaction);
        return transactionRepository.save(transaction);
    }

    public boolean deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
        return true;
    }

    public Transaction updateTransaction(Transaction transaction) {
        transaction.setSymbol(transaction.getSymbol().toUpperCase());
        transaction.setDatetimeUpdated(new Date());
        this.setExecutionPriority(transaction);
        return transactionRepository.save(transaction);
    }

    private void setExecutionPriority(Transaction transaction) {
        switch (transaction.getType().toUpperCase()) {
            case "B": case "S":
                transaction.setExecutionPriority(1);
                break;
            case "TO":
                transaction.setExecutionPriority(2);
                break;
            case "TI":
                transaction.setExecutionPriority(3);
                break;
            default:
                transaction.setExecutionPriority(0);
                break;
        }
    }

}
