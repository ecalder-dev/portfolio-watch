package com.portfoliowatch.service;

import com.portfoliowatch.model.Account;
import com.portfoliowatch.model.Transaction;
import com.portfoliowatch.repository.AccountRepository;
import com.portfoliowatch.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

   public Account createAccount(Account account) {
        account.setAccountId(null);
        account.setDatetimeInserted(new Date());
        account.setDatetimeUpdated(new Date());
        return accountRepository.save(account);
    }

    public List<Account> readAllAccounts() {
        return accountRepository.findAll();
    }

    public Account updateAccount(Account account) {
        account.setDatetimeUpdated(new Date());
        return accountRepository.save(account);
    }

    public boolean deleteAccount(Account account) {
        //prevent deletion if there are still transactions associated to this account.
        List<Transaction> transactions = transactionRepository.findAllByAccountId(account.getAccountId());
        if (transactions.isEmpty()) {
            //accountRepository.delete(account);
        } else {
            return false;
        }
        return true;
    }


}
