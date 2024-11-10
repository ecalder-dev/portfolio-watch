package com.portfoliowatch.service;

import com.portfoliowatch.model.dto.AccountDto;
import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Lot;
import com.portfoliowatch.repository.AccountRepository;
import com.portfoliowatch.repository.LotRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final LotRepository lotRepository;

    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream().map(AccountDto::new).collect(Collectors.toList());
    }

    public AccountDto getAccount(Long id) {
        return accountRepository.findById(id).map(AccountDto::new).orElse(null);
    }

    public AccountDto createAccount(AccountDto accountDto) {
        Account newAccount = accountDto.generateAccount();
        return new AccountDto(accountRepository.save(newAccount));
    }

    public AccountDto updateAccount(AccountDto accountDto) {
        Long id = accountDto.getId();
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id " + id));
        account.setAccountName(accountDto.getAccountName());
        account.setAccountNumber(accountDto.getAccountNumber());
        account.setDateOpened(accountDto.getDateOpened());
        account.setDateClosed(accountDto.getDateClosed());
        account.setIsHidden(accountDto.getIsHidden());
        account.setDatetimeUpdated(new Date());
        return new AccountDto(accountRepository.save(account));
    }

    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id " + id));
        List<Lot> lotList = lotRepository.findAllByAccount(account);
        if (lotList.isEmpty()) {
            accountRepository.delete(account);
        } else {
            log.error("Unable to delete due to existent lots for account = " + account);
        }
    }
}
