package com.portfoliowatch.service;

import com.portfoliowatch.model.Transaction;
import com.portfoliowatch.repository.TransactionRepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

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
        assert(transaction != null);
        assert(transaction.getSymbol() != null);
        assert(transaction.getType() != null);
        switch(transaction.getType().toUpperCase()) {
            case "M":
                assert(isRatioValid(transaction.getRatio()));
                assert(transaction.getNewSymbol() != null);
                break;
            case "SP":
                assert(isRatioValid(transaction.getRatio()));
                break;
        }
        transaction.setSymbol(transaction.getSymbol().toUpperCase());
        transaction.setTransactionId(null);
        transaction.setDatetimeUpdated(new Date());
        transaction.setDatetimeInserted(new Date());
        this.setExecutionPriority(transaction);

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Transaction transaction) {
        assert(transaction != null);
        assert(transaction.getTransactionId() != null);
        assert(transaction.getSymbol() != null);
        assert(transaction.getType() != null);
        switch(transaction.getType().toUpperCase()) {
            case "M":
                assert(isRatioValid(transaction.getRatio()));
                assert(transaction.getNewSymbol() != null);
                break;
            case "SP":
                assert(isRatioValid(transaction.getRatio()));
                break;
        }
        assert(transaction.getDatetimeInserted() != null);
        transaction.setSymbol(transaction.getSymbol().toUpperCase());
        transaction.setDatetimeUpdated(new Date());
        this.setExecutionPriority(transaction);
        return transactionRepository.save(transaction);
    }

    public boolean deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
        return true;
    }

    private boolean isRatioValid(String ratio) {
        if (ratio == null) return false;
        String[] strSeg = ratio.split(":");
        if (strSeg.length != 2) return false;
        return NumberUtils.isCreatable(strSeg[0]) && NumberUtils.isCreatable(strSeg[1]);
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
