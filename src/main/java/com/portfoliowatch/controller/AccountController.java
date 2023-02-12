package com.portfoliowatch.controller;

import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
public class AccountController {

    private final AccountService accountService;

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
            log.error(e.getLocalizedMessage(), e);
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
            log.error(e.getLocalizedMessage(), e);
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
                log.error("Account not found.");
            }
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            log.error(e.getLocalizedMessage(), e);
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
            log.error(e.getLocalizedMessage(), e);
        }
        return new ResponseEntity<>(data, httpStatus);
    }
}
