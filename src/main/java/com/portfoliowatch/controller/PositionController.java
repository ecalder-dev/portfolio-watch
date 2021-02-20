package com.portfoliowatch.controller;

import com.portfoliowatch.model.Position;
import com.portfoliowatch.model.dto.ResponseDto;
import com.portfoliowatch.model.requests.PositionRequest;
import com.portfoliowatch.service.PositiionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/position")
@RestController
public class PositionController {

    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);

    @Autowired
    PositiionService positiionService;

    @GetMapping("portfolio/{portfolioId}")
    public ResponseEntity<ResponseDto<List<Position>>> getPositionsByPortfolioId(@PathVariable("portfolioId")
                                                                                             long portfolioId) {
        List<Position> data = null;
        String error = null;
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            data = positiionService.getTransactionsByPortfolio(portfolioId).get();
        } catch (Exception e) {
            error = e.getLocalizedMessage();
            logger.error(error, e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(new ResponseDto<>(data, error, httpStatus.value()), httpStatus);
    }


    @PostMapping("")
    public ResponseEntity<ResponseDto<Position>> addPosition(@RequestBody PositionRequest request) {
        Position data = null;
        String error = null;
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            data = positiionService.addTransaction(request).get();
        } catch (Exception e) {
            error = e.getLocalizedMessage();
            logger.error(error, e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(new ResponseDto<>(data, error, httpStatus.value()), httpStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<Position>> updatePosition(@PathVariable("id") long id,
                                                                   @RequestBody PositionRequest request) {
        Position data = null;
        String error = null;
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            data = positiionService.updateTransaction(id, request).get();
        } catch (Exception e) {
            error = e.getLocalizedMessage();
            logger.error(error, e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(new ResponseDto<>(data, error, httpStatus.value()), httpStatus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<String>> deletePosition(@PathVariable("id") long id) {
        this.positiionService.deleteTransaction(id);
        return new ResponseEntity<>(new ResponseDto<>("Delete attempted.", null, 200), HttpStatus.OK);
    }
}
