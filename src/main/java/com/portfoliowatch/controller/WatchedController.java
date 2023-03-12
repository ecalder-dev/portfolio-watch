package com.portfoliowatch.controller;

import com.portfoliowatch.model.entity.WatchedSymbol;
import com.portfoliowatch.repository.WatchedRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class WatchedController {

    private WatchedRepository watchedRepository;

    @GetMapping("/watch")
    public ResponseEntity<List<WatchedSymbol>> getWatching() {
        List<WatchedSymbol> data;
        HttpStatus httpStatus;
        try {
            data = watchedRepository.findAll();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @PostMapping("/watch")
    public ResponseEntity<WatchedSymbol> postWatching(@RequestParam String symbol) {
        WatchedSymbol data;
        HttpStatus httpStatus;
        try {
            data = watchedRepository.save(WatchedSymbol.builder().symbol(symbol).build());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }


    @DeleteMapping("/watching")
    public ResponseEntity<Boolean> deleteWatching(@RequestParam String symbol) {
        try {
            watchedRepository.deleteById(symbol);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
