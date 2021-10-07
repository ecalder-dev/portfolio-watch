package com.portfoliowatch.service;

import com.portfoliowatch.model.entity.WatchedSymbol;
import com.portfoliowatch.repository.WatchSymbolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchedService {

    @Autowired
    private WatchSymbolRepository watchSymbolRepository;

    public List<WatchedSymbol> getAllWatchedSymbols() {
        return watchSymbolRepository.findAll();
    }

    public WatchedSymbol createWatchedSymbol(String symbol) {
        WatchedSymbol watchedSymbol = new WatchedSymbol();
        watchedSymbol.setSymbol(symbol);
        return watchSymbolRepository.save(watchedSymbol);
    }

    public boolean deleteWatchedSymbol(String symbol) {
        watchSymbolRepository.deleteById(symbol);
        return true;
    }

}
