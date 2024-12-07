package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.TransferDto;
import com.portfoliowatch.service.TransferService;
import com.portfoliowatch.util.exception.NoDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @GetMapping("/transfers")
    public List<TransferDto> getAllTransfers() {
        return transferService.getAllTransfers();
    }

    @GetMapping("/transfers/{id}")
    public ResponseEntity<TransferDto> getTransferById(@PathVariable Long id) {
        TransferDto transferDto = transferService.getTransferById(id);
        if (transferDto != null) {
            return ResponseEntity.ok(transferDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransferDto> createTransfer(@RequestBody TransferDto transferDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transferService.createTransfer(transferDto));
        } catch (NoDataException e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/transfers")
    public ResponseEntity<TransferDto> updateTransfer(@RequestBody TransferDto transferDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transferService.updateTransfer(transferDto));
        } catch (NoDataException e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/transfers/{id}")
    public ResponseEntity<Void> deleteTransfer(@PathVariable Long id) {
        transferService.deleteTransfer(id);
        return ResponseEntity.noContent().build();
    }
}
