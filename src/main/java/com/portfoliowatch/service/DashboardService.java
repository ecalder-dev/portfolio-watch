package com.portfoliowatch.service;

import com.portfoliowatch.model.Summary;
import com.portfoliowatch.model.financialmodelingprep.FMPProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private FMPService fmpService;

    @Autowired
    private TransactionService transactionService;

    public List<Summary> getSummaryList() throws IOException, URISyntaxException {
        List<Summary> summaries = new ArrayList<>();
        List<FMPProfile> fmpProfiles = fmpService.getCompanyProfile(transactionService.getEquityOwned());
        for (FMPProfile profile: fmpProfiles) {
            summaries.add(new Summary(profile));
        }
        return summaries;
    }


}