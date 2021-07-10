package com.portfoliowatch.service;

import com.portfoliowatch.model.WatchedSymbol;
import com.portfoliowatch.model.dto.QuoteDto;
import com.portfoliowatch.model.financialmodelingprep.FMPProfile;
import com.portfoliowatch.util.Lot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PortfolioStatsService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioStatsService.class);

    @Autowired
    private FMPService fmpService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WatchedSymbolService watchedSymbolService;

    private Date lastSummaryUpdate;

    private final List<QuoteDto> cachedSummaries;

    public PortfolioStatsService() {
        lastSummaryUpdate = null;
        cachedSummaries = new ArrayList<>();
    }

    /**
     * Gets a list of Summaries.
     * @return A Lsit of Summaries
     */
    public List<QuoteDto> getQuoteList() throws IOException, URISyntaxException {
        Date nowDate = new Date();

        //5 minutes
        long cacheRefreshRate = 300000;
        if (lastSummaryUpdate != null && nowDate.getTime() - lastSummaryUpdate.getTime() < cacheRefreshRate) {
            return cachedSummaries;
        }

        logger.info("Generated new quote list.");
        cachedSummaries.clear();

        List<WatchedSymbol> watchedSymbols = watchedSymbolService.getAllWatchedSymbols();
        Set<String> symbols = watchedSymbols.stream().map(WatchedSymbol::getSymbol).collect(Collectors.toSet());
        Set<String> equityOwned = transactionService.getEquityOwned();
        symbols.addAll(equityOwned);
        List<FMPProfile> fmpProfiles = fmpService.getCompanyProfiles(symbols);
        for (FMPProfile profile: fmpProfiles) {
            cachedSummaries.add(new QuoteDto(profile, equityOwned.contains(profile.getSymbol())));
        }
        lastSummaryUpdate = nowDate;
        return cachedSummaries;
    }

    /**
     * Gets a mapping of a total portfolio's sector spread.
     * @return A map with string keys and double values.
     */
    public Map<String, BigDecimal> getSectorSpread() throws IOException, URISyntaxException {
        List<QuoteDto> quoteList = this.getQuoteList();
        Map<String, Lot> symbolLotMap = transactionService.getSymbolAggregatedCostBasisMap();

        Map<String, BigDecimal> sectorSplitMap = new HashMap<>();
        BigDecimal totalPrice = new BigDecimal("0.0");
        for (QuoteDto quote: quoteList) {
            Lot lot = symbolLotMap.get(quote.getSymbol());
            BigDecimal totalShares = new BigDecimal(lot.getShares() + "");
            BigDecimal currentPrice = new BigDecimal(quote.getCurrentPrice() + "");

            totalPrice = totalPrice.add(currentPrice.multiply(totalShares));
            BigDecimal newPrice = sectorSplitMap.get(quote.getSector());
            newPrice = newPrice == null ? new BigDecimal("0.0") : newPrice;
            newPrice = newPrice.add(currentPrice.multiply(totalShares));
            sectorSplitMap.put(quote.getSector(), newPrice);
        }

        for (Map.Entry<String, BigDecimal> keypair: sectorSplitMap.entrySet()){
            keypair.setValue(keypair.getValue().divide(totalPrice, 4));
        }

        return sectorSplitMap;
    }


}