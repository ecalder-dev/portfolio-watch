package com.portfoliowatch.service;

import com.portfoliowatch.model.dto.LotDto;
import com.portfoliowatch.model.dto.TransferDto;
import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Lot;
import com.portfoliowatch.model.entity.Transfer;
import com.portfoliowatch.repository.AccountRepository;
import com.portfoliowatch.repository.LotRepository;
import com.portfoliowatch.repository.TransferRepository;
import com.portfoliowatch.util.ErrorHandler;
import com.portfoliowatch.util.exception.NoDataException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;

    private final AccountRepository accountRepository;

    private final LotRepository lotRepository;

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
        ErrorHandler.validateNonNull(transferDto.getLots(), "TransferDto's lots should not be null.");
        ErrorHandler.validateNonNull(transferDto.getOldAccount(), "TransferDto's oldAccount should not be null.");
        ErrorHandler.validateNonNull(transferDto.getNewAccount(), "TransferDto's newAccount should not be null.");

        List<Lot> lots = lotRepository.findAllById(transferDto.getLots().stream().map(LotDto::getId).collect(Collectors.toList()));
        ErrorHandler.validateTrue(lots.size() == transferDto.getLots().size(), "The number of lots in transaction does not match in database.");
        Account oldAccount = accountRepository.findById(transferDto.getOldAccount().getId())
                .orElseThrow(() -> new NoDataException("Account not found with id " + transferDto.getOldAccount().getId()));
        Account newAccount = accountRepository.findById(transferDto.getNewAccount().getId())
                .orElseThrow(() -> new NoDataException("Account not found with id " + transferDto.getNewAccount().getId()));

        Transfer savedTransfer = transferRepository.save(transferDto.generateTransfer(lots, oldAccount, newAccount));
        lotService.transferLotsWith(savedTransfer);
        return new TransferDto(savedTransfer);
    }

    public TransferDto updateTransfer(TransferDto transferDto) throws NoDataException {
        ErrorHandler.validateNonNull(transferDto.getSymbol(), "TransferDto's symbol should not be null.");
        ErrorHandler.validateNonNull(transferDto.getShares(), "TransferDto's shares should not be null.");
        ErrorHandler.validateNonNull(transferDto.getDateTransacted(), "TransferDto's dateTransacted should not be null.");
        ErrorHandler.validateNonNull(transferDto.getLots(), "TransferDto's lots should not be null.");
        ErrorHandler.validateNonNull(transferDto.getOldAccount(), "TransferDto's oldAccount should not be null.");
        ErrorHandler.validateNonNull(transferDto.getNewAccount(), "TransferDto's newAccount should not be null.");

        Long id = transferDto.getId();
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new NoDataException("Transfer not found with id " + id));
        List<Lot> lots = lotRepository.findAllById(transferDto.getLots().stream().map(LotDto::getId).collect(Collectors.toList()));
        ErrorHandler.validateTrue(lots.size() == transferDto.getLots().size(), "The number of lots in transaction does not match in database.");
        Account oldAccount = accountRepository.findById(transferDto.getOldAccount().getId())
                .orElseThrow(() -> new NoDataException("Account not found with id " + transferDto.getOldAccount().getId()));
        Account newAccount = accountRepository.findById(transferDto.getNewAccount().getId())
                .orElseThrow(() -> new NoDataException("Account not found with id " + transferDto.getNewAccount().getId()));

        transfer.setDateTransacted(transferDto.getDateTransacted());
        transfer.setSymbol(transferDto.getSymbol());
        transfer.setShares(transferDto.getShares());
        transfer.setLots(lots);
        transfer.setOldAccount(oldAccount);
        transfer.setNewAccount(newAccount);
        transfer.setDatetimeUpdated(new Date());
        Transfer savedTransfer = transferRepository.save(transfer);
        lotService.rebuildAllLots();
        return new TransferDto(savedTransfer);
    }

    public void deleteTransfer(Long id) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found with id " + id));
        transferRepository.delete(transfer);
        lotService.rebuildAllLots();
    }
}
