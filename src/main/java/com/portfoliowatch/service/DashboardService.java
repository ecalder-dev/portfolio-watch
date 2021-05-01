package com.portfoliowatch.service;

import com.portfoliowatch.model.Summary;
import com.portfoliowatch.model.Position;
import com.portfoliowatch.model.financialmodelingprep.FMPProfile;
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

    @Autowired
    PositionService positionService;

    @Autowired
    FMPService fmpService;

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
