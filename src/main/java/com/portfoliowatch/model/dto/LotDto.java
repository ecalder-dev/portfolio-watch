package com.portfoliowatch.model.dto;

import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Lot;
import lombok.Data;

import java.util.Date;

@Data
public class LotDto {

    private Long id;
    private String symbol;
    private Double shares;
    private Date dateTransacted;
    private AccountDto account;

    public LotDto(Lot lot) {
        if (lot == null) {
            return;
        }
        this.id = lot.getId();
        this.symbol = lot.getSymbol();
        this.shares = lot.getShares();
        this.dateTransacted = lot.getDateTransacted();
        if (lot.getAccount() != null) {
            this.account = new AccountDto(lot.getAccount());
        }
    }

    public Lot generateLot(Account account) {
        Lot lot = new Lot();
        lot.setSymbol(this.symbol);
        lot.setShares(this.shares);
        lot.setDateTransacted(this.dateTransacted);
        lot.setAccount(account);
        lot.setDatetimeCreated(new Date());
        lot.setDatetimeUpdated(lot.getDatetimeCreated());
        return lot;
    }

}
