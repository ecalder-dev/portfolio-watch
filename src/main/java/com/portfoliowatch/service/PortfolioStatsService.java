package com.portfoliowatch.service;

import com.portfoliowatch.model.dbo.Company;
import com.portfoliowatch.model.dbo.WatchedSymbol;
import com.portfoliowatch.model.dto.QuoteDto;
import com.portfoliowatch.model.nasdaq.CompanyProfile;
import com.portfoliowatch.model.nasdaq.Info;
import com.portfoliowatch.model.nasdaq.InfoData;
import com.portfoliowatch.util.Lot;
import com.portfoliowatch.util.NumberParser;
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
    private NasdaqService nasdaqService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WatchedSymbolService watchedSymbolService;

    @Autowired
    private CompanyService companyService;

    private Date lastSummaryUpdate;

    private final List<QuoteDto> cachedSummaries;

    public PortfolioStatsService() {
        lastSummaryUpdate = null;
        cachedSummaries = new ArrayList<>();
    }

    /**
     * Gets a list of Summaries.
     *
     * @return A Lsit of Summaries
     */
    public List<QuoteDto> getQuoteList() throws IOException {
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
        Map<String, Info> infoMap = nasdaqService.getAllInfo(symbols);
        Map<String, Company> companyMap = this.getCompanyMap(symbols);
        for (String symbol : symbols) {
            QuoteDto quoteDto = new QuoteDto();
            Info info = infoMap.get(symbol);
            Company company = companyMap.get(symbol);
            quoteDto.setSymbol(symbol);
            if (info == null) {
                continue;
            }
            InfoData infoData = info.getPrimaryData() != null ? info.getPrimaryData() : info.getSecondaryData();
            if (infoData != null) {
                quoteDto.setPercentChange(NumberParser.parseDouble(infoData.getPercentageChange()));
                quoteDto.setDollarChange(NumberParser.parseDouble(infoData.getNetChange()));
                quoteDto.setCurrentPrice(NumberParser.parseDouble(infoData.getLastSalePrice()));
            }
            if (info.getKeyStats() != null && info.getKeyStats().getVolume() != null) {
                quoteDto.setAverageVolume(NumberParser.parseLong(info.getKeyStats().getVolume().getValue()));
            }
            String companyName;
            String industry = null;
            String sector = null;
            boolean isEtf = false;
            if (company != null) {
                companyName = company.getName();
                industry = company.getIndustry();
                sector = company.getSector();
            } else {
                companyName = info.getCompanyName();
                isEtf = info.getAssetClass().equalsIgnoreCase("ETF");
                if (isEtf) {
                    industry = "ETF";
                    sector = "ETF";
                }
            }
            quoteDto.setCompanyName(companyName);
            quoteDto.setIndustry(industry);
            quoteDto.setSector(sector);
            quoteDto.setIsEtf(isEtf);
            quoteDto.setIsOwned(equityOwned.contains(symbol));
            cachedSummaries.add(quoteDto);
        }
        lastSummaryUpdate = nowDate;
        return cachedSummaries;
    }

    /**
     * Gets a mapping of a total portfolio's sector spread.
     *
     * @return A map with string keys and double values.
     */
    public Map<String, BigDecimal> getSectorSpread() throws IOException, URISyntaxException {
        List<QuoteDto> quoteList = this.getQuoteList();
        Map<String, Lot> symbolLotMap = transactionService.getSymbolAggregatedCostBasisMap();

        Map<String, BigDecimal> sectorSplitMap = new HashMap<>();
        BigDecimal totalPrice = new BigDecimal("0.0");
        for (QuoteDto quote : quoteList) {
            Lot lot = symbolLotMap.get(quote.getSymbol());
            BigDecimal totalShares = new BigDecimal(lot.getShares() + "");
            BigDecimal currentPrice = new BigDecimal(quote.getCurrentPrice() + "");

            totalPrice = totalPrice.add(currentPrice.multiply(totalShares));
            BigDecimal newPrice = sectorSplitMap.get(quote.getSector());
            newPrice = newPrice == null ? new BigDecimal("0.0") : newPrice;
            newPrice = newPrice.add(currentPrice.multiply(totalShares));
            sectorSplitMap.put(quote.getSector(), newPrice);
        }

        for (Map.Entry<String, BigDecimal> keypair : sectorSplitMap.entrySet()) {
            keypair.setValue(keypair.getValue().divide(totalPrice, 4));
        }

        return sectorSplitMap;
    }

    private Map<String, Company> getCompanyMap(Set<String> symbols) {
        Map<String, Company> map = companyService.getCompanyMap(symbols);
        Set<String> mapKeys = map.keySet();
        for (String symbol: symbols) {
            if (!mapKeys.contains(symbol)) {
                try {
                    CompanyProfile companyProfile = nasdaqService.getCompanyProfile(symbol);
                    if (companyProfile != null) {
                        Company company = new Company();
                        company.setSymbol(companyProfile.getSymbol().getValue());
                        company.setName(companyProfile.getCompanyName().getValue());
                        company.setDescription(companyProfile.getCompanyDescription().getValue());
                        company.setAddress(companyProfile.getAddress().getValue());
                        company.setUrl(companyProfile.getCompanyUrl().getValue());
                        company.setIndustry(companyProfile.getIndustry().getValue());
                        company.setSector(companyProfile.getSector().getValue());
                        company = companyService.createCompany(company);
                        map.put(symbol, company);
                    }
                } catch (IOException e) {
                    logger.error(e.getLocalizedMessage());
                }
            }
        }
        return map;
    }

}