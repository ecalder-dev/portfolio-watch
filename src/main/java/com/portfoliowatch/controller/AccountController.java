package com.portfoliowatch.controller;

import com.portfoliowatch.model.dbo.Account;
import com.portfoliowatch.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

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
            logger.error(e.getLocalizedMessage(), e);
        }
        return new ResponseEntity<>(data, httpStatus);
    }
    
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> readAllAccounts(@RequestParam(required = false) boolean withDetails) {
        List<Account> data;
        HttpStatus httpStatus;
        try {
            data = accountService.readAllAccounts(withDetails);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            logger.error(e.getLocalizedMessage(), e);
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
                logger.error("Account not found.");
            }
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            logger.error(e.getLocalizedMessage(), e);
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
            logger.error(e.getLocalizedMessage(), e);
        }
        return new ResponseEntity<>(data, httpStatus);
    }
}
