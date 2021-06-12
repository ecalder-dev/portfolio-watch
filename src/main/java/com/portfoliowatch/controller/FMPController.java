package com.portfoliowatch.controller;

import com.portfoliowatch.model.financialmodelingprep.FMPProfile;
import com.portfoliowatch.service.FMPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/fmp")
@RestController
public class FMPController {

    private static final Logger logger = LoggerFactory.getLogger(FMPController.class);

    @Autowired
    FMPService fmpService;

    @GetMapping("profiles")
    public ResponseEntity<List<FMPProfile>> getProfiles(@RequestParam Set<String> symbols) {
        List<FMPProfile> data;
        HttpStatus httpStatus;
        try {
            data = fmpService.getCompanyProfile(symbols);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }
}
