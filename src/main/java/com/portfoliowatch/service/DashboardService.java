package com.portfoliowatch.service;

import com.portfoliowatch.model.Account;
import com.portfoliowatch.model.Summary;
import com.portfoliowatch.model.dto.CostBasisDto;
import com.portfoliowatch.model.financialmodelingprep.FMPNews;
import com.portfoliowatch.model.financialmodelingprep.FMPProfile;
import com.portfoliowatch.util.LotList;
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

    public void generateCostBasisMap() {
        costBasisMap.clear();
        List<Account> accountList = accountService.readAllAccounts(true);
        for (Account account: accountList) {
            for (CostBasisDto costBasisDto: account.getCostBasisList()) {
                if (costBasisMap.containsKey(costBasisDto.getSymbol())){
                    CostBasisDto contained = costBasisMap.get(costBasisDto.getSymbol());
                    double total1 = contained.getTotalShares();
                    double total2 = costBasisDto.getTotalShares();
                    double price1 = contained.getAdjustedPrice();
                    double price2 = costBasisDto.getAdjustedPrice();
                    double newPrice = (price1 * total1 + price2 * total2) / (total1 + total2);
                    costBasisDto.setTotalShares(Precision.round(total1 + total2, 2));
                    costBasisDto.setAdjustedPrice(Precision.round(newPrice, 4));
                } else {
                    costBasisMap.put(costBasisDto.getSymbol(), costBasisDto);
                }
            }
        }
    }

}