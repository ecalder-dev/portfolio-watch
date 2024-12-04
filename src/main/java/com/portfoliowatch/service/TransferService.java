package com.portfoliowatch.service;

import com.portfoliowatch.model.dto.TransferDto;
import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Transfer;
import com.portfoliowatch.repository.AccountRepository;
import com.portfoliowatch.repository.TransferRepository;
import com.portfoliowatch.util.ErrorHandler;
import com.portfoliowatch.util.exception.NoDataException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;

    private final AccountRepository accountRepository;

    private final LotService lotService;

    public List<TransferDto> getAllTransfers() {
        return transferRepository.findAll().stream().map(TransferDto::new).collect(Collectors.toList());
    }

    public TransferDto getTransferById(Long id) {
        return transferRepository.findById(id).map(TransferDto::new).orElse(null);
    }

    public TransferDto createTransfer(TransferDto transferDto) throws NoDataException {
        ErrorHandler.validateNonNull(transferDto.getSymbol(), "TransferDto's symbol should not be null.");
        ErrorHandler.validateNonNull(transferDto.getShares(), "TransferDto's shares should not be null.");
        ErrorHandler.validateNonNull(transferDto.getDateTransacted(), "TransferDto's dateTransacted should not be null.");
        ErrorHandler.validateNonNull(transferDto.getFromAccount(), "TransferDto's oldAccount should not be null.");
        ErrorHandler.validateNonNull(transferDto.getToAccount(), "TransferDto's newAccount should not be null.");

        Account oldAccount = accountRepository.findById(transferDto.getFromAccount().getId())
                .orElseThrow(() -> new NoDataException("Account not found with id " + transferDto.getFromAccount().getId()));
        Account newAccount = accountRepository.findById(transferDto.getToAccount().getId())
                .orElseThrow(() -> new NoDataException("Account not found with id " + transferDto.getToAccount().getId()));
        BigDecimal currentTotalShares = lotService.getTotalShares(oldAccount, transferDto.getSymbol());
        ErrorHandler.validateTrue(currentTotalShares.compareTo(transferDto.getShares()) >= 0,
                String.format("There are not enough shares to process transfer. Requested shares: %f, Actual shares: %f.", transferDto.getShares(), currentTotalShares));

        Transfer savedTransfer = transferRepository.save(transferDto.generateTransfer(oldAccount, newAccount));
        return new TransferDto(savedTransfer);
    }

    public TransferDto updateTransfer(TransferDto transferDto) throws NoDataException {
        ErrorHandler.validateNonNull(transferDto.getSymbol(), "TransferDto's symbol should not be null.");
        ErrorHandler.validateNonNull(transferDto.getShares(), "TransferDto's shares should not be null.");
        ErrorHandler.validateNonNull(transferDto.getDateTransacted(), "TransferDto's dateTransacted should not be null.");
        ErrorHandler.validateNonNull(transferDto.getFromAccount(), "TransferDto's oldAccount should not be null.");
        ErrorHandler.validateNonNull(transferDto.getToAccount(), "TransferDto's newAccount should not be null.");

        Long id = transferDto.getId();
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new NoDataException("Transfer not found with id " + id));
        Account oldAccount = accountRepository.findById(transferDto.getFromAccount().getId())
                .orElseThrow(() -> new NoDataException("Account not found with id " + transferDto.getFromAccount().getId()));
        Account newAccount = accountRepository.findById(transferDto.getToAccount().getId())
                .orElseThrow(() -> new NoDataException("Account not found with id " + transferDto.getToAccount().getId()));
        BigDecimal currentTotalShares = lotService.getTotalShares(oldAccount, transferDto.getSymbol());
        ErrorHandler.validateTrue(currentTotalShares.compareTo(transferDto.getShares()) >= 0,
                String.format("There are not enough shares to process transfer. Requested shares: %f, Actual shares: %f.", transferDto.getShares(), currentTotalShares));

        transfer.setDateTransacted(transferDto.getDateTransacted());
        transfer.setSymbol(transferDto.getSymbol());
        transfer.setShares(transferDto.getShares());
        transfer.setFromAccount(oldAccount);
        transfer.setToAccount(newAccount);
        transfer.setDatetimeUpdated(new Date());
        Transfer savedTransfer = transferRepository.save(transfer);
        return new TransferDto(savedTransfer);
    }

    public Date getDateOfLastUpdate() {
        return transferRepository.findLatestDatetimeUpdated();
    }

    public void deleteTransfer(Long id) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found with id " + id));
        transferRepository.delete(transfer);
    }
}
