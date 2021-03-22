package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.ResponseDto;
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

import java.util.List;

@RequestMapping("/fmp")
@RestController
public class FMPController {

    private static final Logger logger = LoggerFactory.getLogger(FMPController.class);

    @Autowired
    FMPService fmpService;

    @GetMapping("profiles")
    public ResponseEntity<ResponseDto<List<FMPProfile>>> positions(@RequestParam List<String> symbols) {
        List<FMPProfile> data;
        String error;
        HttpStatus httpStatus;
        try {
            data = fmpService.getCompanyProfile(symbols);
            error = null;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            error = e.getLocalizedMessage();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(new ResponseDto<>(data, error, httpStatus.value()), httpStatus);
    }

}
