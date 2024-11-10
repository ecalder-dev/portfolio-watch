package com.portfoliowatch.model.dto;

import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.Lot;
import com.portfoliowatch.model.entity.Transaction;
import com.portfoliowatch.model.entity.Transfer;
import com.portfoliowatch.util.enums.TransactionType;
import lombok.Data;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TransactionDto {

    private Long id;
    private Double price;
    private String symbol;
    private Double shares;
    private Date dateTransacted;
    private TransactionType type;
    private AccountDto account;

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.price = transaction.getPrice();
        this.symbol = transaction.getSymbol();
        this.shares = transaction.getShares();
        this.dateTransacted = transaction.getDateTransacted();
        this.type = transaction.getType();
        if (transaction.getAccount() != null) {
            this.account = new AccountDto(transaction.getAccount());
        }
    }

    public Transaction generateTransaction(Account account) {
        Transaction transaction = new Transaction();
        transaction.setPrice(price);
        transaction.setSymbol(symbol);
        transaction.setShares(shares);
        transaction.setDateTransacted(dateTransacted);
        transaction.setType(type);
        transaction.setAccount(account);
        return transaction;
    }

}
