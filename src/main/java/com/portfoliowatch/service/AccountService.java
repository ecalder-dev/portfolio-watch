package com.portfoliowatch.service;

import com.portfoliowatch.model.Account;
import com.portfoliowatch.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

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
        accountRepository.delete(account);
        return true;
    }


}
