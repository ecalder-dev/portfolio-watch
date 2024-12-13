package com.portfoliowatch.service;

import com.portfoliowatch.model.dto.TransactionDto;
import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Transaction;
import com.portfoliowatch.repository.AccountRepository;
import com.portfoliowatch.repository.TransactionRepository;
import com.portfoliowatch.util.ErrorHandler;
import com.portfoliowatch.util.exception.NoDataException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;

  private final AccountRepository accountRepository;

  /**
   * Reads a list of transactions.
   *
   * @return List of transactions.
   */
  public List<TransactionDto> getAllTransactions() {
    return transactionRepository.findAllOrdered().stream()
        .map(TransactionDto::new)
        .collect(Collectors.toList());
  }

  public TransactionDto getTransactionById(Long id) {
    return transactionRepository.findById(id).map(TransactionDto::new).orElse(null);
  }

  public TransactionDto createTransaction(TransactionDto transactionDto) throws NoDataException {
    ErrorHandler.validateNonNull(transactionDto, "TransactionDTO should not be null.");
    ErrorHandler.validateNonNull(
        transactionDto.getSymbol(), "TransactionDTO's symbol should not be null.");
    ErrorHandler.validateNonNull(
        transactionDto.getType(), "TransactionDTO's type should not be null.");
    ErrorHandler.validateNonNull(
        transactionDto.getAccount(), "TransactionDTO's should not be null.");

    Account account =
        accountRepository
            .findById(transactionDto.getAccount().getId())
            .orElseThrow(
                () ->
                    new NoDataException(
                        "Account not found with id " + transactionDto.getAccount().getId()));

    Transaction transaction = transactionDto.generateTransaction(account);
    Transaction savedTransaction = transactionRepository.save(transaction);

    return new TransactionDto(savedTransaction);
  }

  public TransactionDto updateTransaction(TransactionDto transactionDto) throws NoDataException {

    ErrorHandler.validateNonNull(transactionDto, "TransactionDTO should not be null.");
    ErrorHandler.validateNonNull(
        transactionDto.getSymbol(), "TransactionDTO's symbol should not be null.");
    ErrorHandler.validateNonNull(
        transactionDto.getType(), "TransactionDTO's type should not be null.");
    ErrorHandler.validateNonNull(
        transactionDto.getAccount(), "TransactionDTO's should not be null.");

    Transaction transaction =
        transactionRepository
            .findById(transactionDto.getId())
            .orElseThrow(
                () ->
                    new NoDataException("Transaction not found with id " + transactionDto.getId()));
    Account account =
        accountRepository
            .findById(transactionDto.getAccount().getId())
            .orElseThrow(
                () ->
                    new NoDataException(
                        "Account not found with id " + transactionDto.getAccount().getId()));

    transaction.setPrice(transactionDto.getPrice());
    transaction.setSymbol(transactionDto.getSymbol());
    transaction.setShares(transactionDto.getShares());
    transaction.setDateTransacted(transactionDto.getDateTransacted());
    transaction.setType(transactionDto.getType());
    transaction.setAccount(account);
    transaction.setDatetimeUpdated(new Date());
    Transaction savedTransaction = transactionRepository.save(transaction);

    return new TransactionDto(savedTransaction);
  }

  public Date getDateOfLastUpdate() {
    return transactionRepository.findLatestDatetimeUpdated();
  }

  public void deleteTransaction(Long id) throws NoDataException {
    Transaction transaction =
        transactionRepository
            .findById(id)
            .orElseThrow(() -> new NoDataException("Transaction not found with id " + id));
    transactionRepository.delete(transaction);
  }
}
