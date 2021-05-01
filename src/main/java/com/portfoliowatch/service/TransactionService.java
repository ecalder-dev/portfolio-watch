package com.portfoliowatch.service;

import com.portfoliowatch.model.Transaction;
import com.portfoliowatch.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    public List<Transaction> readAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction createTransaction(Transaction transaction) {
        transaction.setTransactionId(null);
        transaction.setDatetimeUpdated(new Date());
        transaction.setDatetimeInserted(new Date());
        return transactionRepository.save(transaction);
    }

    public boolean deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
        return true;
    }

    public Transaction updateTransaction(Transaction transaction) {
        transaction.setDatetimeUpdated(new Date());
        return transactionRepository.save(transaction);
    }
}
