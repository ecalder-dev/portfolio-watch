package com.portfoliowatch.service;

import com.portfoliowatch.model.Position;
import com.portfoliowatch.model.Summary;
import com.portfoliowatch.model.financialmodelingprep.FMPProfile;
import com.portfoliowatch.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private PositionService positionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FMPService fmpService;

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

}