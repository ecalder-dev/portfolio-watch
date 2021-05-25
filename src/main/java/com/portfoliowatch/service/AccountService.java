package com.portfoliowatch.service;

import com.portfoliowatch.model.Account;
import com.portfoliowatch.model.Transaction;
import com.portfoliowatch.repository.AccountRepository;
import com.portfoliowatch.repository.TransactionRepository;
import com.portfoliowatch.util.Lot;
import com.portfoliowatch.util.LotList;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private final Map<Long, Map<String, LotList>> accountSymbolMap = new HashMap<>();

    private final Map<String, List<Lot>> transferMap = new HashMap<>();

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
        List<Transaction> transactions = transactionRepository.findAllOrdered();
        if (transactions.isEmpty()) {
            accountRepository.delete(account);
        } else {
            return false;
        }
        return true;
    }

    public Map<Long, Map<String, LotList>>  generateLotData() {
        List<Transaction> transactionList = transactionRepository.findAllOrdered();
        for (Transaction transaction: transactionList) {
            switch (transaction.getType().toUpperCase()) {
                case "B":
                case "G":
                    doBuy(transaction);
                    break;
                case "S":
                    doSell(transaction);
                    break;
                case "TI":
                    doTransferIn(transaction);
                    break;
                case "TO":
                    doTransferOut(transaction);
                    break;
                case "SP":
                    //TODO: doSplit
                case "MT":
                    //TODO: doMergerTarget
                case "MB":
                    //TODO: doMergerBuyer
                default:
                    break;
            }
        }
        return accountSymbolMap;
    }

    /**
     *A transaction performing a buy.
     * @param transaction The transaction being performed.
     */
    private void doBuy(Transaction transaction) {
        List<Lot> lots = this.getLots(transaction.getAccount().getAccountId(), transaction.getSymbol());
        lots.add(new Lot(transaction.getShares(), transaction.getPrice(), transaction.getDateTransacted()));
    }

    /**
     * A transaction performing a sell.
     * @param transaction The transaction being performed.
     */
    private void doSell(Transaction transaction) {
        LotList lots = this.getLots(transaction.getAccount().getAccountId(), transaction.getSymbol());
        double sellShares = Precision.round(transaction.getShares(), 4);
        while (sellShares > 0 && !lots.isEmpty()) {
            Lot lot = lots.get(0);
            if (lot.getShares() <= sellShares) {
                sellShares = Precision.round(sellShares - lot.getShares(),4);
                lots.remove(lot);
            } else {
                lot.setShares(Precision.round(lot.getShares() - sellShares, 4));
                sellShares = 0;
                if (lot.getShares() == 0) {
                    lots.remove(lot);
                }
            }
        }
        if (sellShares > 0) {
            logger.error("Not enough shares for: " + transaction);
        }
        if (lots.isEmpty()) {
            this.removeSymbolFromAccount(transaction.getAccount().getAccountId(), transaction.getSymbol());
        }
    }

    /**
     *
     * @param transaction The transaction being performed.
     */
    private void doTransferOut(Transaction transaction) {
        List<Lot> transferLots = this.transferMap.computeIfAbsent(transaction.getSymbol(), k -> new LinkedList<>());
        List<Lot> lots = this.getLots(transaction.getAccount().getAccountId(), transaction.getSymbol());
        double sharesToTransfer = transaction.getShares();
        if (lots == null || lots.isEmpty()) {
            logger.error("There are no shares to transfer out for :" + transaction);
        } else {
            List<Lot> toRemove = new ArrayList<>();
            for (Lot transfer: lots) {
                if (sharesToTransfer >= Precision.round(transfer.getShares(), 4)) {
                    sharesToTransfer = Precision.round(sharesToTransfer - transfer.getShares(), 4);
                    toRemove.add(transfer);
                }
            }
            transferLots.addAll(toRemove);
            lots.removeAll(toRemove);
            if (lots.isEmpty()) {
                this.removeSymbolFromAccount(transaction.getAccount().getAccountId(), transaction.getSymbol());
            }
        }
    }

    /**
     *
     * @param transaction The transaction being performed.
     */
    private void doTransferIn(Transaction transaction) {
        List<Lot> transferLots;
        String symbol = transaction.getSymbol();
        if (this.transferMap.containsKey(symbol)) {
            transferLots = this.transferMap.get(symbol);
            if (transferLots.isEmpty()) {
                logger.error("There are no shares to transfer in for :" + transaction);
            } else {
                LotList lots = this.getLots(transaction.getAccount().getAccountId(), transaction.getSymbol());
                double sharesToTransfer = transaction.getShares();
                List<Lot> toRemove = new ArrayList<>();
                for (Lot transfer: transferLots) {
                    if (sharesToTransfer >= Precision.round(transfer.getShares(), 4)) {
                        sharesToTransfer = Precision.round(sharesToTransfer - transfer.getShares(), 4);
                        toRemove.add(transfer);
                    }
                }
                lots.addAll(toRemove);
                transferLots.removeAll(toRemove);
            }
        } else {
            logger.error("There are no transferable stocks for :" + transaction);
        }
    }

    /**
     * Gets a list of lots from the account symbol map. If it doesn't have one, it creates it.
     * @param id The account id.
     * @param symbol The symbol of the stock.
     * @return Returns a list of lot.
     */
    private LotList getLots(Long id, String symbol) {
        LotList lots;
        Map<String, LotList> symbolMap;

        if (accountSymbolMap.containsKey(id)) {
            symbolMap = accountSymbolMap.get(id);
        } else {
            symbolMap = new HashMap<>();
            accountSymbolMap.put(id, symbolMap);
        }

        if (symbolMap.containsKey(symbol)) {
            lots = symbolMap.get(symbol);
        } else {
            lots = new LotList();
            symbolMap.put(symbol, lots);
        }
        return lots;
    }

    private void removeSymbolFromAccount(Long id, String symbol) {
        if (accountSymbolMap.containsKey(id)) {
            Map<String, LotList> symbolMap = accountSymbolMap.get(id);
            if (symbolMap != null) {
                symbolMap.remove(symbol);
            }
        }
    }

}
