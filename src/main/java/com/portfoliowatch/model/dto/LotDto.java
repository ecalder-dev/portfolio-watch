package com.portfoliowatch.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Lot;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class LotDto {

    private UUID id;
    private String symbol;
    private BigDecimal shares;
    private BigDecimal price;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateTransacted;
    private AccountDto account;

    public LotDto(Lot lot) {
        if (lot == null) {
            return;
        }
        this.id = lot.getId();
        this.price = lot.getPrice();
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
