package com.portfoliowatch.controller;

import com.portfoliowatch.model.entity.Transaction;
import com.portfoliowatch.service.PortfolioService;
import com.portfoliowatch.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction data;
        HttpStatus httpStatus;
        try {
            data = transactionService.createTransaction(transaction);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> readAllTransactions() {
        List<Transaction> data;
        HttpStatus httpStatus;
        try {
            data = transactionService.getAllTransactions(null);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("/transaction/{id}")
    public ResponseEntity<Transaction> readAllTransactions(@PathVariable("id") Long id) {
        Transaction data;
        HttpStatus httpStatus;
        try {
            data = transactionService.getTransactionById(id);
            httpStatus = data == null ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @PutMapping("/transaction")
    public ResponseEntity<Transaction> updateTransaction(@RequestBody Transaction transaction) {
        Transaction data;
        HttpStatus httpStatus;
        try {
            data = transactionService.updateTransaction(transaction);
            if (data != null) {
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @DeleteMapping("/transaction")
    public ResponseEntity<Boolean> deleteTransaction(@RequestBody Transaction transaction) {
        boolean data;
        HttpStatus httpStatus;
        try {
            data = transactionService.deleteTransaction(transaction);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = false;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("/regenerateTransactions")
    public void generateAccountLotListMap() {
        transactionService.performAllRecordedTransactions();
    }
}
