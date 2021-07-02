package com.portfoliowatch.service;

import com.portfoliowatch.model.Summary;
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

@Service
public class PortfolioStatsService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioStatsService.class);

    @Autowired
    private FMPService fmpService;

    @Autowired
    private TransactionService transactionService;

    private Date lastSummaryUpdate;

    private final List<Summary> cachedSummaries;

    public PortfolioStatsService() {
        lastSummaryUpdate = null;
        cachedSummaries = new ArrayList<>();
    }

    /**
     * Gets a list of Summaries.
     * @return A Lsit of Summaries
     */
    public List<Summary> getSummaryList() throws IOException, URISyntaxException {
        Date nowDate = new Date();

        //10 minutes
        long cacheRefreshRate = 600000;
        if (lastSummaryUpdate != null && nowDate.getTime() - lastSummaryUpdate.getTime() < cacheRefreshRate) {
            return cachedSummaries;
        }

        logger.info("Getting new summary list.");
        cachedSummaries.clear();
        List<FMPProfile> fmpProfiles = fmpService.getCompanyProfile(transactionService.getEquityOwned());
        Map<String, Lot> symbolLotMap = transactionService.getSymbolAggregatedCostBasisMap();
        for (FMPProfile profile: fmpProfiles) {
            cachedSummaries.add(new Summary(profile, symbolLotMap.get(profile.getSymbol())));
        }
        lastSummaryUpdate = nowDate;
        return cachedSummaries;
    }

    /**
     * Gets a mapping of a total portfolio's sector spread.
     * @return A map with string keys and double values.
     */
    public Map<String, BigDecimal> getSectorSpread() throws IOException, URISyntaxException {
        List<Summary> summaries = this.getSummaryList();
        Map<String, BigDecimal> sectorSplitMap = new HashMap<>();
        BigDecimal totalPrice = new BigDecimal("0.0");
        for (Summary summary: summaries) {
            totalPrice = totalPrice.add(new BigDecimal(summary.getCurrentPrice() + ""));
            BigDecimal currentPrice = sectorSplitMap.get(summary.getSector());
            currentPrice = currentPrice == null ? new BigDecimal("0.0") : currentPrice;
            currentPrice = currentPrice.add(new BigDecimal(summary.getCurrentPrice() + ""));
            sectorSplitMap.put(summary.getSector(), currentPrice);
        }

        for (Map.Entry<String, BigDecimal> keypair: sectorSplitMap.entrySet()){
            keypair.setValue(keypair.getValue().divide(totalPrice, 4));
        }

        return sectorSplitMap;
    }


}