package com.portfoliowatch.service;

import com.portfoliowatch.model.dto.CostBasisDto;
import com.portfoliowatch.model.entity.Lot;
import com.portfoliowatch.model.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Slf4j
@AllArgsConstructor
@Service
public class PortfolioService {

    private final LotService lotService;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final Map<String, List<Lot>> pendingTransfersMap = new TreeMap<>();

    /**
     * Clears current account lot list map and generates a new one.
     * @param transactionList List of transactions to perform.
     */
    public void performTransactions(List<Transaction> transactionList) {
        reset();
        for (Transaction transaction : transactionList) {
            performTransaction(transaction);
        }
    }

    /**
     * Performs a transaction.
     * @param transaction The transaction to perform.
     */
    public void performTransaction(Transaction transaction) {
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
            case "SPN":
                doSpinOff(transaction);
            default:
                break;
        }
    }


    /**
     * A transaction performing a buy.
     *
     * @param transaction The transaction being performed.
     */
    private void doBuy(Transaction transaction) {
        Date now = new Date();
        Lot lot = new Lot();
        lot.setSymbol(transaction.getSymbol());
        lot.setShares(transaction.getShares());
        lot.setPrice(transaction.getPrice());
        lot.setAccount(transaction.getAccount());
        lot.setDatetimeUpdated(now);
        lot.setDatetimeInserted(now);
        lot.setDateTransacted(transaction.getDateTransacted());
        lotService.createLot(lot);
    }

    /**
     * A transaction performing a sell.
     *
     * @param transaction The transaction being performed.
     */
    private void doSell(Transaction transaction) {
        List<Lot> lotList = lotService.getAllLotsBySymbolAndAccount(transaction.getSymbol(), transaction.getAccount());
        double sellShares = Precision.round(transaction.getShares(), 4);
        while (sellShares > 0 && !lotList.isEmpty()) {
            Lot lot = lotList.get(0);
            if (lot.getShares() <= sellShares) {
                sellShares = Precision.round(sellShares - lot.getShares(), 4);
                lotList.remove(0);
                lotService.deleteLot(lot);
            } else {
                lot.setShares(Precision.round(lot.getShares() - sellShares, 4));
                sellShares = 0;
                if (lot.getShares() == 0) {
                    lotList.remove(0);
                    lotService.deleteLot(lot);
                } else {
                    lot.setDatetimeUpdated(new Date());
                    lotService.createLot(lot);
                }
            }
        }
        if (sellShares > 0) {
            logger.error("Not enough shares for: " + transaction);
        }
    }

    /**
     * Do a transfer out transaction.
     *
     * @param transaction The transaction being performed.
     */
    private void doTransferOut(Transaction transaction) {
        List<Lot> pendingTransferLots = this.pendingTransfersMap.computeIfAbsent(transaction.getSymbol(), k -> new LinkedList<>());
        List<Lot> lotList = lotService.getAllLotsBySymbolAndAccount(transaction.getSymbol(), transaction.getAccount());
        double sharesToTransfer = transaction.getShares();
        if (lotList.isEmpty()) {
            logger.error("There are no shares to transfer out for :" + transaction);
        } else {
            List<Lot> toTransfer = new ArrayList<>();
            for (Lot transfer : lotList) {
                if (sharesToTransfer >= Precision.round(transfer.getShares(), 4)) {
                    sharesToTransfer = Precision.round(sharesToTransfer - transfer.getShares(), 4);
                    toTransfer.add(transfer);
                }
            }
            pendingTransferLots.addAll(toTransfer);
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
        if (this.pendingTransfersMap.containsKey(symbol)) {
            transferLots = this.pendingTransfersMap.get(symbol);
            if (transferLots.isEmpty()) {
                logger.error("There are no shares to transfer in for :" + transaction);
            } else {
                double sharesToTransfer = transaction.getShares();
                List<Lot> lotsToUpdate = new ArrayList<>();
                for (Lot lot : transferLots) {
                    if (sharesToTransfer >= Precision.round(lot.getShares(), 4)) {
                        sharesToTransfer = Precision.round(sharesToTransfer - lot.getShares(), 4);
                        lot.setAccount(transaction.getAccount());
                        lot.setDatetimeUpdated(new Date());
                        lotsToUpdate.add(lot);
                    }
                }
                lotService.createLots(lotsToUpdate);
                transferLots.removeAll(lotsToUpdate);
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
        List<Lot> affectedLots = lotService.getAllLotsBySymbolAndAccount(transaction.getSymbol(), transaction.getAccount());
        double multiplier = getMultiplierFromRatio(transaction.getRatio());
        double shareCount = 0;
        for (Lot lot : affectedLots) {
            double multipliedShare = Precision.round(lot.getShares() * multiplier, 4);
            double newPrice = Precision.round((lot.getPrice() * lot.getShares()) / multipliedShare, 4);
            shareCount += multipliedShare;
            lot.setShares(multipliedShare);
            lot.setPrice(newPrice);
            lot.setDatetimeUpdated(new Date());
            lot.setSymbol(transaction.getNewSymbol());
        }
        lotService.createLots(affectedLots);
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
        List<Lot> affectedLots = lotService.getAllLotsBySymbolAndAccount(transaction.getSymbol(), transaction.getAccount());
        double multiplier = getMultiplierFromRatio(transaction.getRatio());
        double beforeTotal = 0;
        double afterTotal = 0;
        for (Lot lot : affectedLots) {
            afterTotal += lot.getShares();
            double newShares = Precision.round(lot.getShares() * multiplier, 4);
            double newPrice = Precision.round(lot.getPrice() / multiplier, 4);
            beforeTotal += newShares;
            lot.setPrice(newPrice);
        }
        lotService.createLots(affectedLots);
        double difference = Math.abs(beforeTotal - afterTotal);
        if (difference % 1 > 0) {
            logger.info(String.format("A difference of %f was cashed out.", difference));
        }
    }

    /**
     * Do a spin-off transaction.
     *
     * @param transaction The transaction being performed.
     */
    private void doSpinOff(Transaction transaction) {
        Date now = new Date();
        double multiplier = getMultiplierFromRatio(transaction.getRatio());
        double parentFairValue = 19.26;
        double childFairValue = 24.43;
        double totalFairValue = parentFairValue + (childFairValue * multiplier);
        double proportionateSpinOffVal = childFairValue * multiplier;
        double adjVal = parentFairValue + proportionateSpinOffVal;
        double spinOffPercent = proportionateSpinOffVal / adjVal;
        List<Lot> affectedLots = lotService.getAllLotsBySymbolAndAccount(transaction.getSymbol(), transaction.getAccount());
        double totalSpinOffPrice = 0.0;
        double totalLot = 0.0;
        for (Lot lot : affectedLots) {
            double currentPrice = lot.getPrice() * lot.getShares();
            totalLot += lot.getShares();
            totalSpinOffPrice += currentPrice * spinOffPercent;
            lot.setPrice(lot.getPrice() * (parentFairValue / totalFairValue));
            lot.setDatetimeUpdated(now);
        }
        lotService.createLots(affectedLots);
        double spinOffCount = totalLot * multiplier;
        double spinOffFrac = spinOffCount % 1;
        double spinOffWhole = spinOffCount - spinOffFrac;

        // Create spin off if there are any whole shares to spin off.
        if (spinOffWhole > 0)  {
            Lot lot = new Lot();
            lot.setSymbol(transaction.getNewSymbol());
            lot.setShares(spinOffWhole);
            lot.setPrice(totalSpinOffPrice/ spinOffCount);
            lot.setAccount(transaction.getAccount());
            lot.setDatetimeUpdated(now);
            lot.setDatetimeInserted(now);
            lot.setDateTransacted(transaction.getDateTransacted());
            lotService.createLot(lot);
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
     * Gets a cost basis map that aggregates all lots regardless of account
     * under their respective symbol.
     * @return A map with symbols as keys.
     */
    public List<CostBasisDto> getCostBasisList(boolean withLotDetails) {
        Set<String> ownedSymbols = lotService.getAllUniqueSymbols();
        List<CostBasisDto> costBasisDtoList = new ArrayList<>();
        for (String symbol: ownedSymbols) {
            List<Lot> lots = lotService.getAllLotsBySymbol(symbol);
            CostBasisDto costBasisDto = new CostBasisDto();
            double totalShares = 0;
            double totalCost = 0;
            for (Lot lot: lots) {
                totalCost += lot.getShares() * lot.getPrice();
                totalShares += lot.getShares();
            }
            totalCost = totalCost / totalShares;
            costBasisDto.setSymbol(symbol);
            costBasisDto.setTotalShares(Precision.round(totalShares, 2));
            costBasisDto.setAdjustedPrice(Precision.round(totalCost, 2));
            if (withLotDetails) {
                costBasisDto.setLotList(lots);
            }
            if (!lots.isEmpty()) {
                costBasisDto.setLatestTransactionDate(lots.get(0).getDateTransacted());
            }
            costBasisDtoList.add(costBasisDto);
        }

        return costBasisDtoList;
    }

    /**
     * Resets the portfolio service.
     * Clears transfer map and deletes all lots from database.
     */
    public void reset() {
        pendingTransfersMap.clear();
        lotService.deleteAllLots();
    }

}
