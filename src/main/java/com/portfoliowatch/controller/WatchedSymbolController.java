package com.portfoliowatch.controller;

import com.portfoliowatch.model.dbo.WatchedSymbol;
import com.portfoliowatch.service.WatchedSymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class WatchedSymbolController {

    @Autowired
    WatchedSymbolService watchedSymbolService;

    @GetMapping("/watching")
    public ResponseEntity<List<WatchedSymbol>> getWatching() {
        List<WatchedSymbol> data;
        HttpStatus httpStatus;
        try {
            data = watchedSymbolService.getAllWatchedSymbols();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @PostMapping("/watching")
    public ResponseEntity<WatchedSymbol> postWatching(@RequestParam String symbol) {
        WatchedSymbol data;
        HttpStatus httpStatus;
        try {
            data = watchedSymbolService.createWatchedSymbol(symbol);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }


    @DeleteMapping("/watching")
    public ResponseEntity<Boolean> deleteWatching(@RequestParam String symbol) {
        boolean data;
        HttpStatus httpStatus;
        try {
            data = watchedSymbolService.deleteWatchedSymbol(symbol);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = false;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }
}
