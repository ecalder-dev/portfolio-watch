package com.portfoliowatch.service;

import com.portfoliowatch.model.entity.Transaction;
import com.portfoliowatch.repository.TransactionRepository;
import com.portfoliowatch.util.StockUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final PortfolioService portfolioService;

    /**
     * Does all the transactions saved in database.
     */
    public void performAllRecordedTransactions() {
        portfolioService.performTransactions(transactionRepository.findAllOrdered());
    }

    /**
     * Reads a list of transactions.
     * @param sort The order of sort.
     * @return List of transactions.
     */
    public List<Transaction> getAllTransactions(Sort sort) {
        if (sort != null) {
            return transactionRepository.findAll(sort);
        } else {
            return transactionRepository.findAll();
        }
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public Transaction createTransaction(Transaction transaction) {
        assert(transaction != null);
        assert(transaction.getSymbol() != null);
        assert(transaction.getType() != null);
        switch(transaction.getType().toUpperCase()) {
            case "M":
                assert(StockUtils.isRatioValid(transaction.getRatio()));
                assert(transaction.getNewSymbol() != null);
                break;
            case "SP":
                assert(StockUtils.isRatioValid(transaction.getRatio()));
                break;
        }
        transaction.setSymbol(transaction.getSymbol().toUpperCase());
        transaction.setTransactionId(null);
        transaction.setDatetimeUpdated(new Date());
        transaction.setDatetimeInserted(new Date());
        this.setExecutionPriority(transaction);
        Transaction savedTransaction = transactionRepository.save(transaction);
        portfolioService.performTransaction(transaction);
        return savedTransaction;
    }

    public Transaction updateTransaction(Transaction transaction) {
        assert(transaction != null);
        assert(transaction.getTransactionId() != null);
        assert(transaction.getSymbol() != null);
        assert(transaction.getType() != null);
        switch(transaction.getType().toUpperCase()) {
            case "M":
                assert(StockUtils.isRatioValid(transaction.getRatio()));
                assert(transaction.getNewSymbol() != null);
                break;
            case "SP":
                assert(StockUtils.isRatioValid(transaction.getRatio()));
                break;
        }
        assert(transaction.getDatetimeInserted() != null);
        transaction.setSymbol(transaction.getSymbol().toUpperCase());
        transaction.setDatetimeUpdated(new Date());
        this.setExecutionPriority(transaction);
        Transaction savedTransaction = transactionRepository.save(transaction);
        performAllRecordedTransactions();
        return savedTransaction;
    }

    public boolean deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
        performAllRecordedTransactions();
        return true;
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
