package com.portfoliowatch.controller;

import com.portfoliowatch.util.Lot;
import com.portfoliowatch.model.Summary;
import com.portfoliowatch.service.DashboardService;
import com.portfoliowatch.util.LotList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/dashboard")
@RestController
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    DashboardService dashboardService;

    @GetMapping("summaries")
    public ResponseEntity<List<Summary>> getSummaries() {
        List<Summary> data;
        HttpStatus httpStatus;
        try {
            data = dashboardService.getSummaryList();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

}
