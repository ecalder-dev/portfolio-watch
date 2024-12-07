package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.TransactionDto;
import com.portfoliowatch.service.TransactionService;
import com.portfoliowatch.util.exception.NoDataException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable("id") Long id) {
        TransactionDto data = transactionService.getTransactionById(id);
        if (data != null) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/transactions")
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transactionDto));
        } catch (NoDataException e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("/transactions")
    public ResponseEntity<TransactionDto> updateTransaction(@RequestBody TransactionDto transactionDto) {
        try {
            return ResponseEntity.ok(transactionService.updateTransaction(transactionDto));
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (NoDataException e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
