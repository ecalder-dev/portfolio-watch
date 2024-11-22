package com.portfoliowatch.model.dto;

import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Lot;
import com.portfoliowatch.model.entity.Transfer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class TransferDto {

    private Long id;
    private String symbol;
    private Double shares;
    private Date dateTransacted;
    private List<LotDto> lots;
    private AccountDto oldAccount;
    private AccountDto newAccount;

    public TransferDto(Transfer transfer) {
        this.id = transfer.getId();
        this.symbol = transfer.getSymbol();
        this.shares = transfer.getShares();
        this.dateTransacted = transfer.getDateTransacted();
        this.lots = new LinkedList<>();
        if (transfer.getLots() != null) {
            this.lots = transfer.getLots().stream().map(LotDto::new).collect(Collectors.toList());
        }
        if (transfer.getOldAccount() != null) {
            oldAccount = new AccountDto(transfer.getOldAccount());
        }
        if (transfer.getNewAccount() != null) {
            newAccount = new AccountDto(transfer.getNewAccount());
        }
    }

    public Transfer generateTransfer(List<Lot> lots, Account oldAccount, Account newAccount) {
        Transfer transfer = new Transfer();
        transfer.setSymbol(symbol);
        transfer.setShares(shares);
        transfer.setDateTransacted(dateTransacted);
        transfer.setLots(lots);
        transfer.setOldAccount(oldAccount);
        transfer.setNewAccount(newAccount);
        transfer.setDatetimeCreated(new Date());
        transfer.setDatetimeUpdated(transfer.getDatetimeCreated());
        return transfer;
    }

}
