package com.portfoliowatch.controller;

import com.portfoliowatch.model.nasdaq.CompanyProfile;
import com.portfoliowatch.model.nasdaq.DividendProfile;
import com.portfoliowatch.model.nasdaq.StockInfo;
import com.portfoliowatch.service.NasdaqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequestMapping("/api/nasdaq")
@RestController
public class NasdaqController {

    private static final Logger logger = LoggerFactory.getLogger(NasdaqController.class);

    @Autowired
    private NasdaqService nasdaqApi;

    @GetMapping("dividend-profile")
    public ResponseEntity<DividendProfile> getDividendProfile(@RequestParam String symbol) {
        DividendProfile data;
        HttpStatus httpStatus;
        try {
            data = nasdaqApi.getDividendProfile(symbol);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("dividend-profiles")
    public ResponseEntity<Map<String, DividendProfile>> getDividendProfiles(@RequestParam Set<String> symbol) {
        Map<String, DividendProfile> data;
        HttpStatus httpStatus;
        try {
            data = nasdaqApi.getDividendProfiles(symbol);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("company-profile")
    public ResponseEntity<CompanyProfile> getCompanyProfile(@RequestParam String symbol) {
        CompanyProfile data;
        HttpStatus httpStatus;
        try {
            data = nasdaqApi.getCompanyProfile(symbol);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("company-profiles")
    public ResponseEntity<List<CompanyProfile>> getCompanyProfiles(@RequestParam Set<String> symbols) {
        List<CompanyProfile> data;
        HttpStatus httpStatus;
        try {
            data = nasdaqApi.getCompanyProfiles(symbols);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("info")
    public ResponseEntity<StockInfo> getInfo(@RequestParam String symbol) {
        StockInfo data;
        HttpStatus httpStatus;
        try {
            data = nasdaqApi.getInfo(symbol);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("info/all")
    public ResponseEntity<Map<String, StockInfo>> getAllInfo(@RequestParam Set<String> symbols) {
        Map<String, StockInfo> data;
        HttpStatus httpStatus;
        try {
            data = nasdaqApi.getAllInfo(symbols);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

}
