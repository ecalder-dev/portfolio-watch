package com.portfoliowatch.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfoliowatch.model.entity.LotSale;
import com.portfoliowatch.util.enums.LotSaleType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LotSaleDto {

  private UUID id;
  private String symbol;
  private LotSaleType type;
  private BigDecimal soldShares;
  private BigDecimal purchasedPrice;
  private BigDecimal purchasedPriceYen;
  private BigDecimal totalPurchasedPrice;
  private BigDecimal totalPurchasedPriceYen;
  private BigDecimal soldPrice;
  private BigDecimal soldPriceYen;
  private BigDecimal totalSoldPrice;
  private BigDecimal totalSoldPriceYen;
  private BigDecimal totalPriceDifference;
  private BigDecimal totalPriceDifferenceYen;
  private Integer taxYear;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date dateTransacted;

  public LotSaleDto(
      String symbol,
      Date dateTransacted,
      BigDecimal totalPurchasedPrice,
      BigDecimal totalSoldPrice,
      BigDecimal totalPriceDifference) {
    this.symbol = symbol;
    this.dateTransacted = dateTransacted;
    this.totalPurchasedPrice = totalPurchasedPrice;
    this.totalSoldPrice = totalSoldPrice;
    this.totalPriceDifference = totalPriceDifference;
  }

  public LotSaleDto(LotSale lotSale) {
    if (lotSale == null) {
      return;
    }
    this.id = lotSale.getId();
    this.symbol = lotSale.getSymbol();
    this.type = lotSale.getType();
    this.soldShares = lotSale.getSoldShares();
    this.purchasedPrice = lotSale.getPurchasedPrice();
    this.totalPurchasedPrice = lotSale.getTotalPurchasedPrice();
    this.soldPrice = lotSale.getSoldPrice();
    this.totalSoldPrice = lotSale.getTotalSoldPrice();
    this.totalPriceDifference = lotSale.getTotalPriceDifference();
    this.taxYear = lotSale.getTaxYear();
    this.dateTransacted = lotSale.getDateTransacted();
  }

  public void applyYenRate(BigDecimal usdToYenRate) {
    this.purchasedPriceYen = this.purchasedPrice.multiply(usdToYenRate);
    this.totalPurchasedPriceYen = this.totalPurchasedPrice.multiply(usdToYenRate);
    this.soldPriceYen = this.soldPrice.multiply(usdToYenRate);
    this.totalSoldPriceYen = this.totalSoldPrice.multiply(usdToYenRate);
    this.totalPriceDifferenceYen = this.totalPriceDifference.multiply(usdToYenRate);
  }
}
