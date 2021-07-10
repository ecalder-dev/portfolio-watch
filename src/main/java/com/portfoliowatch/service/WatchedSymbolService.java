package com.portfoliowatch.service;

import com.portfoliowatch.model.WatchedSymbol;
import com.portfoliowatch.repository.WatchSymbolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchedSymbolService {

    @Autowired
    private WatchSymbolRepository watchSymbolRepository;

    public List<WatchedSymbol> getAllWatchedSymbols() {
        return watchSymbolRepository.findAll();
    }

    public WatchedSymbol createWatchedSymbol(WatchedSymbol watchedSymbol) {
        return watchSymbolRepository.save(watchedSymbol);
    }

    public boolean deleteWatchedSymbol(WatchedSymbol watchedSymbol) {
        watchSymbolRepository.delete(watchedSymbol);
        return true;
    }

}
