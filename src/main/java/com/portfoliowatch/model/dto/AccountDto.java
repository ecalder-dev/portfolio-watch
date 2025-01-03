package com.portfoliowatch.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.portfoliowatch.model.dto.deserializer.LocalDateDeserializer;
import com.portfoliowatch.model.entity.Account;
import java.time.LocalDate;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDto {

  private Long id;
  private String accountName;
  private String accountNumber;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate dateOpened;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate dateClosed;

  private Boolean isHidden;

  public AccountDto(Account account) {
    this.id = account.getId();
    this.accountName = account.getAccountName();
    this.accountNumber = account.getAccountNumber();
    this.dateOpened = account.getDateOpened();
    this.dateClosed = account.getDateClosed();
    this.isHidden = account.getIsHidden();
  }

  public Account generateAccount() {
    Account account = new Account();
    account.setAccountName(this.accountName);
    account.setAccountNumber(this.accountNumber);
    account.setDateOpened(this.dateOpened);
    account.setDateClosed(this.dateClosed);
    account.setIsHidden(this.isHidden);
    account.setDatetimeCreated(new Date());
    account.setDatetimeUpdated(account.getDatetimeCreated());
    return account;
  }
}
