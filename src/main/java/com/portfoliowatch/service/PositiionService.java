package com.portfoliowatch.service;

import com.portfoliowatch.model.Position;
import com.portfoliowatch.model.requests.PositionRequest;
import com.portfoliowatch.repository.PositionRepository;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class PositiionService {

    private static final Logger logger = LoggerFactory.getLogger(PositiionService.class);

    @Autowired
    private PositionRepository transactionRepository;

    /**
     * Inserts a new transaction.
     *
     * @param positionRequest the request to insert
     * @return The added Transaction.
     */
    @Async
    public CompletableFuture<Position> addTransaction(PositionRequest positionRequest) {
        Validate.notNull(positionRequest);
        Validate.notNull(positionRequest.getPortfolioId());
        Validate.notNull(positionRequest.getCost());
        Validate.notNull(positionRequest.getShareCount());
        Validate.isTrue(positionRequest.getShareCount() > 0);
        Validate.isTrue(positionRequest.getCost() > 0);
        Validate.notEmpty(positionRequest.getTicker());

        Position position = new Position();
        position.setPortfolioId(positionRequest.getPortfolioId());
        position.setTicker(positionRequest.getTicker());
        position.setShareCount(positionRequest.getShareCount());
        position.setCostBasis(positionRequest.getCost());
        position.setDateInserted(new Date());
        position.setDateUpdated(position.getDateInserted());
        return CompletableFuture.completedFuture(transactionRepository.saveAndFlush(position));
    }

    /**
     * Updates a new transaction.
     * @para, transactionId the id to update
     * @param positionRequest the request to update
     * @return The updated Transaction.
     */
    @Async
    public CompletableFuture<Position> updateTransaction(Long transactionId, PositionRequest positionRequest) {
        Validate.notNull(positionRequest);
        Validate.notNull(positionRequest.getPortfolioId());
        Validate.notNull(positionRequest.getCost());
        Validate.notNull(positionRequest.getShareCount());
        Validate.notEmpty(positionRequest.getTicker());

        Optional<Position> optionalTransaction = this.transactionRepository.findById(transactionId);
        if (!optionalTransaction.isPresent()) {
            return CompletableFuture.completedFuture(null);
        }
        Position position = optionalTransaction.get();
        position.setPortfolioId(positionRequest.getPortfolioId());
        position.setTicker(positionRequest.getTicker());
        position.setShareCount(positionRequest.getShareCount());
        position.setCostBasis(positionRequest.getCost());
        position.setDateUpdated(position.getDateInserted());
        return CompletableFuture.completedFuture(transactionRepository.saveAndFlush(position));
    }

    /**
     * Removes a transaction.
     * @param transactionId the id to delete record by
     */
    @Async
    public void deleteTransaction(long transactionId) {
        this.transactionRepository.deleteById(transactionId);
    }

    /**
     * Gets a list of transactions given by portfolioId.
     * @param portfolioId the id of the portfolio
     * @return list of Transactions related to portfolio id
     */
    @Async
    public CompletableFuture<List<Position>> getTransactionsByPortfolio(Long portfolioId) {
        Validate.notNull(portfolioId);
        List<Position> positions = this.transactionRepository.findAllByPortfolioId(portfolioId);
        return CompletableFuture.completedFuture(positions);
    }


}
