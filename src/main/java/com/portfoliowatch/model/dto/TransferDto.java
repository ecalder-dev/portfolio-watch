package com.portfoliowatch.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Transfer;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransferDto {

  private Long id;
  private String symbol;
  private BigDecimal shares;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date dateTransacted;

  private AccountDto fromAccount;
  private AccountDto toAccount;

  public TransferDto(Transfer transfer) {
    this.id = transfer.getId();
    this.symbol = transfer.getSymbol();
    this.shares = transfer.getShares();
    this.dateTransacted = transfer.getDateTransacted();
    if (transfer.getFromAccount() != null) {
      fromAccount = new AccountDto(transfer.getFromAccount());
    }
    if (transfer.getToAccount() != null) {
      toAccount = new AccountDto(transfer.getToAccount());
    }
  }

  public Transfer generateTransfer(Account oldAccount, Account newAccount) {
    Transfer transfer = new Transfer();
    transfer.setSymbol(symbol);
    transfer.setShares(shares);
    transfer.setDateTransacted(dateTransacted);
    transfer.setFromAccount(oldAccount);
    transfer.setToAccount(newAccount);
    transfer.setDatetimeCreated(new Date());
    transfer.setDatetimeUpdated(transfer.getDatetimeCreated());
    return transfer;
  }
}
