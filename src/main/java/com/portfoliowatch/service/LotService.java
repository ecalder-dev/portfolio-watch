package com.portfoliowatch.service;

import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Lot;
import com.portfoliowatch.repository.LotRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class LotService {

    private final LotRepository lotRepository;
    private final Sort sortByTransactionDateAsc = Sort.by(Sort.Direction.ASC, "dateTransacted");

    public List<Lot> getAllLots() {
        return lotRepository.findAll(sortByTransactionDateAsc);
    }

    public List<Lot> getAllLotsBySymbol(String symbol) {
        return lotRepository.findAllBySymbol(sortByTransactionDateAsc, symbol);
    }

    public List<Lot> getAllLotsBySymbolAndAccount(String symbol, Account account) {
        return lotRepository.findAllBySymbolAndAccount(sortByTransactionDateAsc, symbol, account);
    }

    public Lot createLot(Lot lot) {
        return lotRepository.save(lot);
    }

    public List<Lot> createLots(List<Lot> lots) {
        return lotRepository.saveAll(lots);
    }

    public void deleteLot(Lot lot) {
        lotRepository.delete(lot);
    }

    public void deleteAllLots() {
        lotRepository.deleteAll();
    }

    public Set<String> getAllUniqueSymbols() {
        return lotRepository.findAllUniqueSymbols();
    }
}
