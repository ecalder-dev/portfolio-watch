package com.portfoliowatch.service;

import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Lot;
import com.portfoliowatch.model.entity.Transaction;
import com.portfoliowatch.model.entity.Transfer;
import com.portfoliowatch.repository.AccountRepository;
import com.portfoliowatch.repository.CorporateActionRepository;
import com.portfoliowatch.repository.LotRepository;
import com.portfoliowatch.repository.TransactionRepository;
import com.portfoliowatch.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class LotServiceTest {

    private LotRepository lotRepository;
    private TransactionRepository transactionRepository;
    private TransferRepository transferRepository;
    private CorporateActionRepository corporateActionRepository;
    private AccountRepository accountRepository;
    private LotService lotService;

    @BeforeEach
    public void setup() {
        lotRepository = mock(LotRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        transferRepository = mock(TransferRepository.class);
        corporateActionRepository = mock(CorporateActionRepository.class);
        accountRepository = mock(AccountRepository.class);
        lotService = new LotService(lotRepository, transactionRepository, transferRepository, corporateActionRepository, accountRepository);
    }

    @Test
    public void test_LotService_transferValid() {
        Account account1 = new Account();
        account1.setId(1L);
        Account account2 = new Account();
        account2.setId(2L);

        Lot lot1 = new Lot();
        lot1.setDateTransacted(new Date());
        lot1.setSymbol("TEST");
        lot1.setAccount(account1);
        lot1.setId(UUID.randomUUID());
        lot1.setShares(new BigDecimal("1.97"));

        Lot lot2 = new Lot();
        lot2.setDateTransacted(new Date());
        lot2.setSymbol("TEST");
        lot2.setAccount(account1);
        lot2.setId(UUID.randomUUID());
        lot2.setShares(new BigDecimal("0.01"));

        Lot lot3 = new Lot();
        lot3.setDateTransacted(new Date());
        lot3.setSymbol("TEST");
        lot3.setAccount(account1);
        lot3.setId(UUID.randomUUID());
        lot3.setShares(new BigDecimal("0.01"));

        Lot lot4 = new Lot();
        lot4.setDateTransacted(new Date());
        lot4.setSymbol("TEST");
        lot4.setAccount(account1);
        lot4.setId(UUID.randomUUID());
        lot4.setShares(new BigDecimal("0.01"));

        List<Lot> oldAccountLots = List.of(lot1, lot2, lot3, lot4);

        doReturn(oldAccountLots).when(lotRepository).findBySymbolAndAccountOrderByDateTransactedAsc(anyString(), any(Account.class));

        Transfer transfer = new Transfer();
        transfer.setId(1L);
        transfer.setSymbol("TEST");
        transfer.setShares(new BigDecimal("2.00"));
        transfer.setFromAccount(account1);
        transfer.setToAccount(account2);
        lotService.transferLotsWith(transfer);
    }
}
