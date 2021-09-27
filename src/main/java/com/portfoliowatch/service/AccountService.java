package com.portfoliowatch.service;

import com.portfoliowatch.model.dbo.Account;
import com.portfoliowatch.model.dbo.Transaction;
import com.portfoliowatch.model.dto.CostBasisDto;
import com.portfoliowatch.repository.AccountRepository;
import com.portfoliowatch.util.LotList;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    public Account createAccount(Account account) {
        account.setAccountId(null);
        account.setDatetimeInserted(new Date());
        account.setDatetimeUpdated(new Date());
        return accountRepository.save(account);
    }

    public List<Account> readAllAccounts(boolean withDetails) {
        List<Account> accounts = accountRepository.findAll();
        if (withDetails) {
            for (Account account: accounts) {
                this.insertCostBasisInfo(account);
            }
        }
        return accounts;
    }

    public Account updateAccount(Account account) {
        account.setDatetimeUpdated(new Date());
        return accountRepository.save(account);
    }

    public boolean deleteAccount(Account account) {
        List<Transaction> transactions = transactionService.readAllTransactions(null);
        if (transactions.isEmpty()) {
            accountRepository.delete(account);
        } else {
            return false;
        }
        return true;
    }

    public void insertCostBasisInfo(Account account) {
        Map<String, LotList> symbols = transactionService.getAccountLotListMap().get(account.getAccountId());
        List<CostBasisDto> costBasisDtoList = new ArrayList<>();
        for (Map.Entry<String, LotList> keypair: symbols.entrySet()) {
            LotList lotList = keypair.getValue();
            CostBasisDto costBasisDto = new CostBasisDto();
            costBasisDto.setSymbol(keypair.getKey());
            costBasisDto.setLotList(lotList);
            costBasisDto.setTotalShares(Precision.round(lotList.getTotalShares(), 2));
            costBasisDto.setAdjustedPrice(Precision.round(lotList.getTotalPrice() / lotList.getTotalShares(), 4));
            costBasisDtoList.add(costBasisDto);
        }
        account.setCostBasisList(costBasisDtoList);
    }

}
