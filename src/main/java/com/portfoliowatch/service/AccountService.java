package com.portfoliowatch.service;

import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Transaction;
import com.portfoliowatch.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final PortfolioService costBasisService;

    private final TransactionService transactionService;

    public Account createAccount(Account account) {
        account.setAccountId(null);
        account.setDatetimeInserted(new Date());
        account.setDatetimeUpdated(new Date());
        return accountRepository.save(account);
    }

    public List<Account> readAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts;
    }

    public Account updateAccount(Account account) {
        account.setDatetimeUpdated(new Date());
        return accountRepository.save(account);
    }

    public boolean deleteAccount(Account account) {
        List<Transaction> transactions = transactionService.getAllTransactions(null);
        if (transactions.isEmpty()) {
            accountRepository.delete(account);
        } else {
            return false;
        }
        return true;
    }
}
