package com.portfoliowatch.service;

import com.portfoliowatch.api.NasdaqAPI;
import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Transaction;
import com.portfoliowatch.model.dto.CostBasisDto;
import com.portfoliowatch.model.nasdaq.DividendProfile;
import com.portfoliowatch.repository.AccountRepository;
import com.portfoliowatch.model.dto.LotList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AccountService {

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
        Map<String, DividendProfile> dividendProfileMap = new HashMap<>();
        try {
            dividendProfileMap = NasdaqAPI.getDividendProfiles(symbols.keySet());
        } catch (IOException e) {
            log.error("Error getting dividend profiles: {}", symbols.keySet());
        }
        List<CostBasisDto> costBasisDtoList = new ArrayList<>();
        for (Map.Entry<String, LotList> keypair: symbols.entrySet()) {
            LotList lotList = keypair.getValue();
            DividendProfile dividendProfile = dividendProfileMap.get(keypair.getKey());
            CostBasisDto costBasisDto = new CostBasisDto();
            costBasisDto.setSymbol(keypair.getKey());
            costBasisDto.setLotList(lotList);
            costBasisDto.setTotalShares(Precision.round(lotList.getTotalShares(), 2));
            costBasisDto.setAdjustedPrice(Precision.round(lotList.getTotalPrice() / lotList.getTotalShares(), 4));
            costBasisDto.setTotalAnnualDividend(lotList.getTotalShares() * dividendProfile.getAnnualizedDividend());
            costBasisDtoList.add(costBasisDto);
            account.setTotalAnnualDividends(account.getTotalAnnualDividends() + costBasisDto.getTotalAnnualDividend());
        }
        account.setCostBasisList(costBasisDtoList);
    }

}
