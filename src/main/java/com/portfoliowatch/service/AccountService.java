package com.portfoliowatch.service;

import com.portfoliowatch.model.Account;
import com.portfoliowatch.model.Transaction;
import com.portfoliowatch.model.dto.CostBasisDto;
import com.portfoliowatch.repository.AccountRepository;
import com.portfoliowatch.repository.TransactionRepository;
import com.portfoliowatch.util.Lot;
import com.portfoliowatch.util.LotList;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Map<Long, Map<String, LotList>> costBasisMap = null;

    private Map<String, List<Lot>> transferMap = null;

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
        List<Transaction> transactions = transactionRepository.findAllOrdered();
        if (transactions.isEmpty()) {
            accountRepository.delete(account);
        } else {
            return false;
        }
        return true;
    }

    public void insertCostBasisInfo(Account account) {
        if (this.costBasisMap == null) {
            this.regenerateCostBasisMap();
        }
        Map<String, LotList> symbols = this.costBasisMap.get(account.getAccountId());
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

    private void regenerateCostBasisMap() {
        costBasisMap = new TreeMap<>();
        transferMap = new TreeMap<>();
        List<Transaction> transactionList = transactionRepository.findAllOrdered();
        for (Transaction transaction : transactionList) {
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
                    doSplit(transaction);
                    break;
                case "M":
                    doMerger(transaction);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * A transaction performing a buy.
     *
     * @param transaction The transaction being performed.
     */
    private void doBuy(Transaction transaction) {
        LotList lots = this.getLots(transaction.getAccount().getAccountId(), transaction.getSymbol());
        lots.add(new Lot(transaction.getShares(), transaction.getPrice(), transaction.getDateTransacted()));
    }

    /**
     * A transaction performing a sell.
     *
     * @param transaction The transaction being performed.
     */
    private void doSell(Transaction transaction) {
        LotList lots = this.getLots(transaction.getAccount().getAccountId(), transaction.getSymbol());
        double sellShares = Precision.round(transaction.getShares(), 4);
        while (sellShares > 0 && !lots.isEmpty()) {
            Lot lot = lots.peak();
            if (lot.getShares() <= sellShares) {
                sellShares = Precision.round(sellShares - lot.getShares(), 4);
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
     * Do a transfer out transaction.
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
            for (Lot transfer : lots) {
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
     * Does a transfer in transaction.
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
                for (Lot transfer : transferLots) {
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
     * Do a merger transaction.
     *
     * @param transaction The transaction being performed.
     */
    private void doMerger(Transaction transaction) {
        LotList affectedLot = this.getLots(transaction.getAccount().getAccountId(), transaction.getSymbol());
        LotList transferringLot = this.getLots(transaction.getAccount().getAccountId(), transaction.getNewSymbol());
        List<Lot> moving = new ArrayList<>();

        double multiplier = getMultiplierFromRatio(transaction.getRatio());
        double shareCount = 0;
        for (Lot lot : affectedLot) {
            double multipliedShare = Precision.round(lot.getShares() * multiplier, 4);
            double newPrice = Precision.round((lot.getPrice() * lot.getShares()) / multipliedShare, 4);
            shareCount += multipliedShare;
            lot.setShares(multipliedShare);
            lot.setPrice(newPrice);
            moving.add(lot);
        }
        transferringLot.addAll(moving);
        affectedLot.removeAll(moving);
        this.removeSymbolFromAccount(transaction.getAccount().getAccountId(), transaction.getSymbol());

        double partials = shareCount % 1;
        if (partials > 0) {
            Transaction sellTransaction = new Transaction();
            sellTransaction.setSymbol(transaction.getNewSymbol());
            sellTransaction.setShares(partials);
            sellTransaction.setPrice(transaction.getPrice());
            sellTransaction.setAccount(transaction.getAccount());
            this.doSell(sellTransaction);
        }
    }

    /**
     * Do a split transaction.
     *
     * @param transaction The transaction being performed.
     */
    private void doSplit(Transaction transaction) {
        LotList affectedLot = this.getLots(transaction.getAccount().getAccountId(), transaction.getSymbol());
        double multiplier = getMultiplierFromRatio(transaction.getRatio());
        double beforeTotal = 0;
        double afterTotal = 0;
        for (Lot lot : affectedLot) {
            afterTotal += lot.getShares();
            double newShares = Precision.round(lot.getShares() * multiplier, 4);
            double newPrice = Precision.round(lot.getPrice() / multiplier, 4);
            beforeTotal += newShares;
            lot.setPrice(newPrice);
        }
        double difference = Math.abs(beforeTotal - afterTotal);
        if (difference % 1 > 0) {
            logger.info(String.format("A difference of %f was cashed out.", difference));
        }
    }

    /**
     * Creates a multiplier based on a ratio string.
     * @param ratio The ratio string that contains a : between values.
     * @return A double created from the ratio string.
     */
    private double getMultiplierFromRatio(String ratio) {
        assert (ratio != null);
        String[] strArr = ratio.split(":");
        return Double.parseDouble(strArr[1]) / Double.parseDouble(strArr[0]);
    }

    /**
     * Gets a list of lots from the account symbol map. If it doesn't have one, it creates it.
     *
     * @param id     The account id.
     * @param symbol The symbol of the stock.
     * @return Returns a list of lot.
     */
    private LotList getLots(Long id, String symbol) {
        LotList lots;
        Map<String, LotList> symbolMap;

        if (costBasisMap.containsKey(id)) {
            symbolMap = costBasisMap.get(id);
        } else {
            symbolMap = new TreeMap<>();
            costBasisMap.put(id, symbolMap);
        }

        if (symbolMap.containsKey(symbol)) {
            lots = symbolMap.get(symbol);
        } else {
            lots = new LotList();
            symbolMap.put(symbol, lots);
        }
        return lots;
    }

    /**
     * Remove a symbol from a an account.
     *
     * @param id     The account id.
     * @param symbol The symbol of a company.
     */
    private void removeSymbolFromAccount(Long id, String symbol) {
        if (costBasisMap.containsKey(id)) {
            Map<String, LotList> symbolMap = costBasisMap.get(id);
            if (symbolMap != null) {
                symbolMap.remove(symbol);
            }
        }
    }

}
