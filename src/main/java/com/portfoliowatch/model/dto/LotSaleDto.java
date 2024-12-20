package com.portfoliowatch.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfoliowatch.model.entity.LotSale;
import com.portfoliowatch.util.enums.Currency;
import com.portfoliowatch.util.enums.LotSaleType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LotSaleDto {

  private static final int JPY_SCALE = 0;
  private static final int USD_SCALE = 2;
  private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

  private UUID id;
  private String symbol;
  private LotSaleType type;
  private BigDecimal soldShares;
  private Map<Currency, BigDecimal> acquisitionPrice = new HashMap<>();
  private Map<Currency, BigDecimal> totalAcquisitionPrice = new HashMap<>();
  private Map<Currency, BigDecimal> soldPrice = new HashMap<>();
  private Map<Currency, BigDecimal> totalSoldPrice = new HashMap<>();
  private Map<Currency, BigDecimal> totalPriceDifference = new HashMap<>();

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateAcquired;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateSold;

  private Integer taxYear;

  public LotSaleDto(LotSale lotSale) {
    if (lotSale == null) {
      return;
    }
    this.id = lotSale.getId();
    this.symbol = lotSale.getSymbol();
    this.type = lotSale.getType();
    this.soldShares = lotSale.getSoldShares();
    this.acquisitionPrice.put(
        Currency.USD, lotSale.getAcquisitionPrice().setScale(USD_SCALE, ROUNDING));
    this.totalAcquisitionPrice.put(
        Currency.USD, lotSale.getTotalAcquisitionPrice().setScale(USD_SCALE, ROUNDING));
    this.soldPrice.put(Currency.USD, lotSale.getSoldPrice().setScale(USD_SCALE, ROUNDING));
    this.totalSoldPrice.put(
        Currency.USD, lotSale.getTotalSoldPrice().setScale(USD_SCALE, ROUNDING));
    this.totalPriceDifference.put(
        Currency.USD, lotSale.getTotalPriceDifference().setScale(USD_SCALE, ROUNDING));
    this.taxYear = lotSale.getTaxYear();
    this.dateAcquired = lotSale.getDateAcquired();
    this.dateSold = lotSale.getDateSold();
  }

  /**
   * Applies the given currency conversion rates to the acquisition and sale prices, as well as
   * their totals. The method converts the prices from USD to the target currency (JPY in this case)
   * and updates the relevant price maps accordingly.
   *
   * @param currency The target currency to apply the conversion rates.
   * @param acquiredRate The conversion rate to apply to the acquisition price.
   * @param soldRate The conversion rate to apply to the sold price.
   */
  public void applyCurrency(Currency currency, BigDecimal acquiredRate, BigDecimal soldRate) {
    // Convert all prices using the provided rates for the target currency (JPY)
    convertToCurrency(this.acquisitionPrice, acquiredRate);
    convertToCurrency(this.totalAcquisitionPrice, acquiredRate);
    convertToCurrency(this.soldPrice, soldRate);
    convertToCurrency(this.totalSoldPrice, soldRate);

    // Calculate and store the price difference between total sold and total acquisition prices in
    // JPY
    BigDecimal priceDifference =
        this.totalSoldPrice
            .get(Currency.JPY)
            .subtract(this.totalAcquisitionPrice.get(Currency.JPY))
            .setScale(JPY_SCALE, ROUNDING);

    // Store the calculated price difference for the target currency
    this.totalPriceDifference.put(currency, priceDifference);
  }

  /**
   * Helper method to convert the USD price in the price map to the target currency (JPY).
   *
   * @param currencyPriceMap The map containing the price in USD, which will be converted to JPY.
   * @param rate The conversion rate to apply to the USD price.
   */
  private void convertToCurrency(Map<Currency, BigDecimal> currencyPriceMap, BigDecimal rate) {
    // Retrieve the price in USD from the map
    BigDecimal priceInUsd = currencyPriceMap.get(Currency.USD);

    // Validate input to prevent null values
    if (priceInUsd == null || rate == null) {
      throw new IllegalArgumentException("Price in USD and rate cannot be null.");
    }

    // Convert the price from USD to JPY using the given rate, and set the scale for JPY
    BigDecimal convertedPrice = priceInUsd.multiply(rate).setScale(JPY_SCALE, ROUNDING);

    // Store the converted price in JPY in the map
    currencyPriceMap.put(Currency.JPY, convertedPrice);
  }
}
