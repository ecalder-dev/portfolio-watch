package com.portfoliowatch.service;

import com.portfoliowatch.service.thirdparty.NasdaqAPI;
import com.portfoliowatch.model.dto.QuoteDto;
import com.portfoliowatch.model.entity.Company;
import com.portfoliowatch.model.nasdaq.InfoData;
import com.portfoliowatch.model.nasdaq.StockInfo;
import com.portfoliowatch.model.nasdaq.Summary;
import com.portfoliowatch.util.parser.NumberParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class QuotesService {

    private final CompanyService companyService;

    /**
     * Gets a list of quotes.
     *
     * @return A list of Summaries
     */
    public List<QuoteDto> getQuotes(Set<String> symbols) throws IOException, InterruptedException {
        List<QuoteDto> quoteDtoList = new ArrayList<>();
        Map<String, StockInfo> infoMap = NasdaqAPI.getAllInfo(symbols);
        List<Company> companyList = this.companyService.getCompanies(symbols);

        for (String symbol : symbols) {
            StockInfo stockInfo = infoMap.get(symbol);
            Company company = companyList.stream().filter(c -> c.getSymbol() != null &&
                    c.getSymbol().equalsIgnoreCase(symbol)).findFirst().orElse(null);
            Summary summary = NasdaqAPI.getSummary(symbol);
            QuoteDto quoteDto = generateQuoteDto(symbol, company, stockInfo, summary);
            if (quoteDto != null) {
                quoteDtoList.add(quoteDto);
            }
        }
        return quoteDtoList;
    }

    /**
     * Gets stock quote information given a symbol.
     *
     * @return The quote
     */
    @Cacheable("quote")
    public QuoteDto getQuote(String symbol) throws IOException, InterruptedException {
        StockInfo stockInfo = NasdaqAPI.getInfo(symbol);
        Summary summary = NasdaqAPI.getSummary(symbol);
        Company company = this.companyService.getCompany(symbol);
        return generateQuoteDto(symbol, company, stockInfo, summary);
    }

    private QuoteDto generateQuoteDto(String symbol, Company company, StockInfo stockInfo, Summary summary) {
        String ETF = "ETF";
        QuoteDto quoteDto = new QuoteDto();
        quoteDto.setSymbol(symbol.toUpperCase());
        if (company != null) {
            quoteDto.setCompanyName(company.getName());
            if (!StringUtils.isEmpty(company.getIndustry())) {
                quoteDto.setIndustry(company.getIndustry());
            } else {
                quoteDto.setIndustry("N/A");
            }
            if (!StringUtils.isEmpty(company.getSector())) {
                quoteDto.setSector(company.getSector());
            } else {
                quoteDto.setSector("N/A");
            }
            quoteDto.setIsEtf(false);
        } else if (stockInfo != null && stockInfo.getAssetClass().equalsIgnoreCase(ETF)){
            quoteDto.setIndustry(ETF);
            quoteDto.setSector(ETF);
            quoteDto.setCompanyName("N/A");
            quoteDto.setIsEtf(true);
        } else {
            return null;
        }

        if (stockInfo != null) {
            InfoData infoData = stockInfo.getPrimaryData() != null &&
                    !stockInfo.getPrimaryData().getLastTradeTimestamp().contains("AFTER HOURS") ?
                    stockInfo.getPrimaryData() : stockInfo.getSecondaryData();

            if (infoData != null) {
                quoteDto.setPercentChange(NumberParser.parseDouble(infoData.getPercentageChange()));
                quoteDto.setDollarChange(NumberParser.parseDouble(infoData.getNetChange()));
                quoteDto.setCurrentPrice(NumberParser.parseDouble(infoData.getLastSalePrice()));
            }
        }

        if (summary != null && summary.getSummaryData() != null) {
            String avgVolStr = summary.getSummaryData().getAverageVolume() != null ?
                    summary.getSummaryData().getAverageVolume().getValue() : summary.getSummaryData().getAvgDailyVol20Days().getValue();
            Long avgVol = NumberParser.parseLong(avgVolStr);
            quoteDto.setAverageVolume(avgVol);
        }
        return quoteDto;
    }

}