package com.portfoliowatch.service;

import com.portfoliowatch.model.entity.Transaction;
import com.portfoliowatch.repository.TransactionRepository;
import com.portfoliowatch.model.dto.Lot;
import com.portfoliowatch.model.dto.LotList;
import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private TransactionRepository transactionRepository;

    @Getter
    private final Map<Long, Map<String, LotList>> accountLotListMap = new TreeMap<>();

    @Getter
    private final Set<String> equityOwned = new HashSet<>();

    private final Map<String, List<Lot>> transferMap = new TreeMap<>();

    @Autowired
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.generateAccountLotListMap();
    }

    /**
     * Reads a list of transactions.
     * @param sort The order of sort.
     * @return
     */
    public List<Transaction> readAllTransactions(Sort sort) {
        if (sort != null) {
            return transactionRepository.findAll(sort);
        } else {
            return transactionRepository.findAll();
        }
    }

    public Transaction readTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public Transaction createTransaction(Transaction transaction) {
        assert(transaction != null);
        assert(transaction.getSymbol() != null);
        assert(transaction.getType() != null);
        switch(transaction.getType().toUpperCase()) {
            case "M":
                assert(isRatioValid(transaction.getRatio()));
                assert(transaction.getNewSymbol() != null);
                break;
            case "SP":
                assert(isRatioValid(transaction.getRatio()));
                break;
        }
        transaction.setSymbol(transaction.getSymbol().toUpperCase());
        transaction.setTransactionId(null);
        transaction.setDatetimeUpdated(new Date());
        transaction.setDatetimeInserted(new Date());
        this.setExecutionPriority(transaction);

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Transaction transaction) {
        assert(transaction != null);
        assert(transaction.getTransactionId() != null);
        assert(transaction.getSymbol() != null);
        assert(transaction.getType() != null);
        switch(transaction.getType().toUpperCase()) {
            case "M":
                assert(isRatioValid(transaction.getRatio()));
                assert(transaction.getNewSymbol() != null);
                break;
            case "SP":
                assert(isRatioValid(transaction.getRatio()));
                break;
        }
        assert(transaction.getDatetimeInserted() != null);
        transaction.setSymbol(transaction.getSymbol().toUpperCase());
        transaction.setDatetimeUpdated(new Date());
        this.setExecutionPriority(transaction);
        return transactionRepository.save(transaction);
    }

    public boolean deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
        return true;
    }

    private boolean isRatioValid(String ratio) {
        if (ratio == null) return false;
        String[] strSeg = ratio.split(":");
        if (strSeg.length != 2) return false;
        return NumberUtils.isCreatable(strSeg[0]) && NumberUtils.isCreatable(strSeg[1]);
    }

    private void setExecutionPriority(Transaction transaction) {
        switch (transaction.getType().toUpperCase()) {
            case "B": case "S":
                transaction.setExecutionPriority(1);
                break;
            case "TO":
                transaction.setExecutionPriority(2);
                break;
            case "TI":
                transaction.setExecutionPriority(3);
                break;
            default:
                transaction.setExecutionPriority(0);
                break;
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
     * Clears current account lot list map and generates a new one.
     */
    public void generateAccountLotListMap() {
        logger.info("Generating new cost basis map.");
        accountLotListMap.clear();
        transferMap.clear();
        equityOwned.clear();
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

        for (Map.Entry<Long, Map<String, LotList>> keypair : accountLotListMap.entrySet()) {
            this.equityOwned.addAll(keypair.getValue().keySet());
        }
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

        if (accountLotListMap.containsKey(id)) {
            symbolMap = accountLotListMap.get(id);
        } else {
            symbolMap = new TreeMap<>();
            accountLotListMap.put(id, symbolMap);
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
        if (accountLotListMap.containsKey(id)) {
            Map<String, LotList> symbolMap = accountLotListMap.get(id);
            if (symbolMap != null) {
                symbolMap.remove(symbol);
            }
        }
    }

    /**
     * Gets a cost basis map that aggregates all lots regardless of account
     * under their respective symbol.
     * @return A map with symbols as keys.
     */
    public Map<String, Lot> getSymbolAggregatedCostBasisMap() {
        Map<String, Lot> aggregatedLotMap = new TreeMap<>();
        for (Map.Entry<Long, Map<String, LotList>> account : accountLotListMap.entrySet()) {
            for (Map.Entry<String, LotList> symbolLotList: account.getValue().entrySet()) {
                Lot aggregatedLot = aggregatedLotMap.get(symbolLotList.getKey());
                if (aggregatedLot == null) {
                    aggregatedLot = new Lot();
                    aggregatedLot.setPrice(0.0);
                    aggregatedLot.setShares(0.0);
                    aggregatedLotMap.put(symbolLotList.getKey(), aggregatedLot);
                }

                for (Lot lot: symbolLotList.getValue()) {
                    double total1 = aggregatedLot.getShares();
                    double total2 = lot.getShares();
                    double price1 = aggregatedLot.getPrice();
                    double price2 = lot.getPrice();
                    double newPrice = (price1 * total1 + price2 * total2) / (total1 + total2);
                    aggregatedLot.setShares(Precision.round(total1 + total2, 2));
                    aggregatedLot.setPrice(Precision.round(newPrice, 4));
                }
            }
        }

        return aggregatedLotMap;
    }

}
