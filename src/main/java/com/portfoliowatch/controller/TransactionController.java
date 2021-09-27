package com.portfoliowatch.controller;

import com.portfoliowatch.model.dbo.Transaction;
import com.portfoliowatch.service.TransactionService;
import com.portfoliowatch.util.Lot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

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
            data = transactionService.readAllTransactions(null);
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
            data = transactionService.readTransactionById(id);
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

    @GetMapping("/transaction/cost-basis")
    public ResponseEntity<Map<String, Lot>> getSymbolAggregatedCostBasisMap() {
        Map<String, Lot> data = transactionService.getSymbolAggregatedCostBasisMap();
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
