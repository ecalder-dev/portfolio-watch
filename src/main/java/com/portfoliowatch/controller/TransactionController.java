package com.portfoliowatch.controller;

import com.portfoliowatch.model.Transaction;
import com.portfoliowatch.model.dto.ResponseDto;
import com.portfoliowatch.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping("/transactions")
    public ResponseEntity<ResponseDto<List<Transaction>>> readAllTransactions() {
        List<Transaction> data;
        String error;
        HttpStatus httpStatus;
        try {
            data = transactionService.readAllTransactions();
            error = null;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            error = e.getLocalizedMessage();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(new ResponseDto<>(data, error, httpStatus.value()), httpStatus);
    }

    @PostMapping("/transaction")
    public ResponseEntity<ResponseDto<Transaction>> createTransaction(@RequestBody Transaction transaction) {
        Transaction data;
        String error;
        HttpStatus httpStatus;
        try {
            data = transactionService.createTransaction(transaction);
            error = null;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            error = e.getLocalizedMessage();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(new ResponseDto<>(data, error, httpStatus.value()), httpStatus);
    }

    @PutMapping("/transaction")
    public ResponseEntity<ResponseDto<Transaction>> updateTransaction(@RequestBody Transaction transaction) {
        Transaction data;
        String error;
        HttpStatus httpStatus;
        try {
            data = transactionService.updateTransaction(transaction);
            if (data != null) {
                error = null;
                httpStatus = HttpStatus.OK;
            } else {
                error = "Transaction id " + transaction.getTransactionId() +
                        " was not updated because it doesn't exist.";
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        } catch (Exception e) {
            data = null;
            error = e.getLocalizedMessage();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(new ResponseDto<>(data, error, httpStatus.value()), httpStatus);
    }

    @DeleteMapping("/transaction")
    public ResponseEntity<ResponseDto<Boolean>> deleteTransaction(@RequestBody Transaction transaction) {
        boolean data;
        String error;
        HttpStatus httpStatus;
        try {
            data = transactionService.deleteTransaction(transaction);
            error = null;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = false;
            error = e.getLocalizedMessage();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(new ResponseDto<>(data, error, httpStatus.value()), httpStatus);
    }
}
