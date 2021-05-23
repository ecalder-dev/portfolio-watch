package com.portfoliowatch.service;

import com.portfoliowatch.model.Lot;
import com.portfoliowatch.model.Position;
import com.portfoliowatch.model.Summary;
import com.portfoliowatch.model.Transaction;
import com.portfoliowatch.model.financialmodelingprep.FMPProfile;
import com.portfoliowatch.repository.TransactionRepository;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private PositionService positionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FMPService fmpService;

    private final Map<Long, Map<String, List<Lot>>> accountSymbolMap = new HashMap<>();

    private final Map<String, List<Lot>> transferMap = new HashMap<>();

    public List<Summary> getSummaryList() throws IOException, URISyntaxException {
        List<Summary> summaries = new ArrayList<>();
        List<Position> positions = positionService.readAllPositions();
        List<String> symbols = positions.stream().map(Position::getSymbol).collect(Collectors.toList());
        List<FMPProfile> fmpProfiles = fmpService.getCompanyProfile(symbols);

        for (Position position: positions) {
            Optional<FMPProfile> fmpProfileOptional = fmpProfiles.stream()
                    .filter(fp -> fp.getSymbol().equalsIgnoreCase(position.getSymbol()))
                    .findFirst();
            FMPProfile fmpProfile = fmpProfileOptional.orElse(null);
            summaries.add(new Summary(position, fmpProfile));
        }

        return summaries;
    }

    public Map<Long, Map<String, List<Lot>>>  generateLotData() {
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
                case "M":
                    //TODO: doMerger
                default:
                    break;
            }
        }
        return accountSymbolMap;
    }

    /**
     *
     * @param transaction The transaction being performed.
     */
    private void doBuy(Transaction transaction) {
        List<Lot> lots = this.getLots(transaction.getAccount().getAccountId(), transaction.getSymbol());
        lots.add(new Lot(transaction.getShares(), transaction.getPrice(), transaction.getDateTransacted()));
    }

    /**
     *
     * @param transaction The transaction being performed.
     */
    private void doSell(Transaction transaction) {
        List<Lot> lots = this.getLots(transaction.getAccount().getAccountId(), transaction.getSymbol());
        double sellShares = transaction.getShares();
        while (sellShares > 0 && !lots.isEmpty()) {
            Lot lot = lots.get(0);
            if (lot.getShares() <= sellShares) {
                sellShares -= lot.getShares();
                lots.remove(lot);
            } else {
                lot.setShares(lot.getShares() - sellShares);
                sellShares = 0;
            }
        }
        if (sellShares > 0) {
            System.err.println("NOT ENOUGH SHARES!!!: " + transaction.getSymbol());
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
            System.err.println("There are no shares to transfer out for :" + transaction.getSymbol());
        } else {
            List<Lot> toRemove = new ArrayList<>();
            for (Lot transfer: lots) {
                if (sharesToTransfer >= Precision.round(transfer.getShares(), 4)) {
                    sharesToTransfer = Precision.round(sharesToTransfer - transfer.getShares(), 4);
                    transferLots.add(transfer);
                    toRemove.add(transfer);
                }
            }
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
                System.err.println("The stack is empty for symbol: " + symbol);
            } else {
                List<Lot> lots = this.getLots(transaction.getAccount().getAccountId(), transaction.getSymbol());
                double sharesToTransfer = transaction.getShares();
                List<Lot> toRemove = new ArrayList<>();
                for (Lot transfer: transferLots) {
                    if (sharesToTransfer >= Precision.round(transfer.getShares(), 4)) {
                        sharesToTransfer = Precision.round(sharesToTransfer - transfer.getShares(), 4);
                        lots.add(transfer);
                        toRemove.add(transfer);
                    }
                }
                transferLots.removeAll(toRemove);
            }
        } else {
            System.err.println("There are no transferable stocks with symbol: " + symbol);
        }
    }

    /**
     * Gets a list of lots from the account symbol map. If it doesn't have one, it creates it.
     * @param id The account id.
     * @param symbol The symbol of the stock.
     * @return Returns a list of lot.
     */
    private List<Lot> getLots(Long id, String symbol) {
        List<Lot> lots;
        Map<String, List<Lot>> symbolMap;

        if (accountSymbolMap.containsKey(id)) {
            symbolMap = accountSymbolMap.get(id);
        } else {
            symbolMap = new HashMap<>();
            accountSymbolMap.put(id, symbolMap);
        }

        if (symbolMap.containsKey(symbol)) {
            lots = symbolMap.get(symbol);
        } else {
            lots = new LinkedList<>();
            symbolMap.put(symbol, lots);
        }
        return lots;
    }

    private void removeSymbolFromAccount(Long id, String symbol) {
        if (accountSymbolMap.containsKey(id)) {
            Map<String, List<Lot>> symbolMap = accountSymbolMap.get(id);
            if (symbolMap != null) {
                symbolMap.remove(symbol);
            }
        }
    }

}