package com.portfoliowatch.service;

import com.portfoliowatch.api.NasdaqAPI;
import com.portfoliowatch.model.dto.QuoteDto;
import com.portfoliowatch.model.entity.Company;
import com.portfoliowatch.model.entity.WatchedSymbol;
import com.portfoliowatch.model.nasdaq.InfoData;
import com.portfoliowatch.model.nasdaq.StockInfo;
import com.portfoliowatch.model.nasdaq.Summary;
import com.portfoliowatch.model.nasdaq.SummaryData;
import com.portfoliowatch.util.NumberParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WatchedService watchedSymbolService;

    @Autowired
    private CompanyService companyService;

    /**
     * Gets a list of Summaries.
     *
     * @return A Lsit of Summaries
     */
    @Cacheable("quote-list")
    public List<QuoteDto> getQuoteList() throws IOException {
        List<QuoteDto> quoteDtos = new ArrayList<>();

        Set<String> symbols = watchedSymbolService.getAllWatchedSymbols().stream().map(WatchedSymbol::getSymbol).collect(Collectors.toSet());
        Set<String> equityOwned = transactionService.getEquityOwned();
        symbols.addAll(transactionService.getEquityOwned());

        Map<String, StockInfo> infoMap = NasdaqAPI.getAllInfo(symbols);
        List<Company> companyList = this.companyService.getCompanies(symbols);

        for (String symbol : symbols) {
            QuoteDto quoteDto = new QuoteDto();
            StockInfo stockInfo = infoMap.get(symbol);
            Company company = companyList.stream().filter(c -> c.getSymbol() != null &&
                    c.getSymbol().equalsIgnoreCase(symbol)).findFirst().orElse(null);
            quoteDto.setSymbol(symbol);
            if (stockInfo == null) {
                continue;
            }
            Summary summary = NasdaqAPI.getSummary(symbol);
            InfoData infoData = stockInfo.getPrimaryData() != null &&
                    !stockInfo.getPrimaryData().getLastTradeTimestamp().contains("AFTER HOURS") ?
                    stockInfo.getPrimaryData() : stockInfo.getSecondaryData();

            if (infoData != null) {
                quoteDto.setPercentChange(NumberParser.parseDouble(infoData.getPercentageChange()));
                quoteDto.setDollarChange(NumberParser.parseDouble(infoData.getNetChange()));
                quoteDto.setCurrentPrice(NumberParser.parseDouble(infoData.getLastSalePrice()));
            }
            if (summary != null && summary.getSummaryData() != null) {
                String avgVolStr = summary.getSummaryData().getAverageVolume() != null ?
                        summary.getSummaryData().getAverageVolume().getValue() : summary.getSummaryData().getAvgDailyVol20Days().getValue();
                Long avgVol = NumberParser.parseLong(avgVolStr);
                quoteDto.setAverageVolume(avgVol);
            }
            quoteDto.setCompanyName(company != null ? company.getName() : stockInfo.getCompanyName());
            quoteDto.setIndustry(company != null && company.getIndustry() != null ? company.getIndustry() : "ETF");
            quoteDto.setSector(company != null && company.getSector() != null ? company.getSector() : "ETF");
            quoteDto.setIsEtf(quoteDto.getIndustry().equals("ETF"));
            quoteDto.setIsOwned(equityOwned.contains(symbol));
            quoteDtos.add(quoteDto);
        }
        return quoteDtos;
    }

}