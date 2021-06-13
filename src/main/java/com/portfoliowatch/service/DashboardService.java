package com.portfoliowatch.service;

import com.portfoliowatch.model.Account;
import com.portfoliowatch.model.Position;
import com.portfoliowatch.model.Summary;
import com.portfoliowatch.model.dto.CostBasisDto;
import com.portfoliowatch.model.financialmodelingprep.FMPNews;
import com.portfoliowatch.model.financialmodelingprep.FMPProfile;
import com.portfoliowatch.util.LotList;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private FMPService fmpService;

    private final Map<String, CostBasisDto> costBasisMap = new HashMap<>();

    public List<Summary> getSummaryList() throws IOException, URISyntaxException {
        List<Summary> summaries = new ArrayList<>();
        this.generateCostBasisMap();
        List<FMPProfile> fmpProfiles = fmpService.getCompanyProfile(costBasisMap.keySet());
        for (Map.Entry<String, CostBasisDto> keypair: costBasisMap.entrySet()) {
            Optional<FMPProfile> fmpProfileOptional = fmpProfiles.stream()
                    .filter(fp -> fp.getSymbol().equalsIgnoreCase(keypair.getKey()))
                    .findFirst();
            FMPProfile fmpProfile = fmpProfileOptional.orElse(null);
            summaries.add(new Summary(keypair.getValue(), fmpProfile));
        }
        return summaries;
    }

    public List<FMPNews> getPositionNews() throws IOException, URISyntaxException {
        Map<Long, Map<String, LotList>> costBasisMap = accountService.getCostBasisMap();
        Set<String> symbols = new HashSet<>();
        for (Map.Entry<Long,
                Map<String, LotList>> keypair: costBasisMap.entrySet()) {
            symbols.addAll(keypair.getValue().keySet());
        }
        return this.fmpService.getNews(symbols, 3);
    }

    public void generateCostBasisMap() {

    }

}