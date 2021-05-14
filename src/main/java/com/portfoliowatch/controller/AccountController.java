package com.portfoliowatch.controller;

import com.portfoliowatch.model.Account;
import com.portfoliowatch.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class AccountController {

    @Autowired
    AccountService accountService;

    @PostMapping("/account")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account data;
        HttpStatus httpStatus;
        try {
            data = accountService.createAccount(account);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }
    
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> readAllAccounts() {
        List<Account> data;
        HttpStatus httpStatus;
        try {
            data = accountService.readAllAccounts();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @PutMapping("/account")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account) {
        Account data;
        HttpStatus httpStatus;
        try {
            data = accountService.updateAccount(account);
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

    @DeleteMapping("/account")
    public ResponseEntity<Boolean> deleteAccount(@RequestBody Account account) {
        boolean data;
        HttpStatus httpStatus;
        try {
            data = accountService.deleteAccount(account);
            httpStatus = data ? HttpStatus.OK : HttpStatus.CONFLICT;
        } catch (Exception e) {
            data = false;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }
}
