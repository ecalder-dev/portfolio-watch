package com.portfoliowatch.service;

import com.portfoliowatch.model.dto.AggregatedAnnualSaleDto;
import com.portfoliowatch.model.dto.LotSaleDto;
import com.portfoliowatch.model.entity.Lot;
import com.portfoliowatch.model.entity.LotSale;
import com.portfoliowatch.model.entity.fx.ExchangeRate;
import com.portfoliowatch.model.entity.fx.ExchangeRateId;
import com.portfoliowatch.repository.LotSaleRepository;
import com.portfoliowatch.service.fx.ExchangeRateService;
import com.portfoliowatch.util.enums.Currency;
import com.portfoliowatch.util.enums.LotSaleType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LotSaleService {

  private final LotSaleRepository lotSaleRepository;

  private final ExchangeRateService exchangeRateService;

  private Map<LocalDate, BigDecimal> dateRatioMap;

  protected void recordLotSold(
      Lot lot,
      BigDecimal soldShares,
      BigDecimal soldPrice,
      LocalDate dateSold,
      LotSaleType lotSaleType) {
    // Initialize LotSale object
    LotSale lotSale = new LotSale();
    String symbol = lot.getSymbol();
    LocalDate dateAcquired = lot.getDateTransacted();
    BigDecimal acquisitionPrice = lot.getPrice();
    BigDecimal totalAcquisitionPrice = acquisitionPrice.multiply(soldShares);
    BigDecimal totalSoldPrice = soldPrice.multiply(soldShares);
    BigDecimal totalPriceDifference = totalSoldPrice.subtract(totalAcquisitionPrice);
    Integer taxYear = dateSold.getYear();
    // Set values in lotSale object
    lotSale.setSymbol(symbol);
    lotSale.setType(lotSaleType);
    lotSale.setAcquisitionPrice(acquisitionPrice);
    lotSale.setTotalAcquisitionPrice(totalAcquisitionPrice);
    lotSale.setDateAcquired(dateAcquired);
    lotSale.setSoldShares(soldShares);
    lotSale.setSoldPrice(soldPrice);
    lotSale.setTotalSoldPrice(totalSoldPrice);
    lotSale.setTotalPriceDifference(totalPriceDifference);
    lotSale.setTaxYear(taxYear);
    lotSale.setDateSold(dateSold);
    lotSale.setLot(lot);
    lotSale.setDatetimeCreated(new Date());
    lotSale.setDatetimeUpdated(lotSale.getDatetimeCreated());
    lotSaleRepository.save(lotSale);
  }

  public List<Integer> getAllAvailableTaxYears() {
    return lotSaleRepository.findAllDistinctTaxYear();
  }

  public List<LotSaleDto> getLotSalesByYear(Integer year) {
    List<LotSaleDto> lotSaleDtoList =
        lotSaleRepository.findLotSalesByTaxYearOrderByDateSoldAsc(year).stream()
            .map(LotSaleDto::new)
            .toList();
    if (!lotSaleDtoList.isEmpty()) {
      Map<ExchangeRateId, BigDecimal> dateRateMap = new HashMap<>();
      for (LotSaleDto lotSaleDto : lotSaleDtoList) {
        BigDecimal acquiredRate = getYenRate(dateRateMap, lotSaleDto.getDateAcquired());
        BigDecimal soldRate = getYenRate(dateRateMap, lotSaleDto.getDateSold());
        lotSaleDto.applyCurrency(Currency.JPY, acquiredRate, soldRate);
      }
    }
    return lotSaleDtoList;
  }

  public Map<Integer, AggregatedAnnualSaleDto> getAllAggregatedAnnualSales() {
    List<Integer> years = getAllAvailableTaxYears();
    Map<Integer, AggregatedAnnualSaleDto> aggregatedAnnualSaleDtoMap = new HashMap<>();
    for (Integer year : years) {
      aggregatedAnnualSaleDtoMap.put(
          year, generateAggregatedAnnualSaleDto(getLotSalesByYear(year)));
    }
    return aggregatedAnnualSaleDtoMap;
  }

  public AggregatedAnnualSaleDto getAggregatedAnnualSaleByYear(Integer year) {
    return generateAggregatedAnnualSaleDto(getLotSalesByYear(year));
  }

  protected void deleteAll() {
    lotSaleRepository.deleteAll();
  }

  private BigDecimal getYenRate(Map<ExchangeRateId, BigDecimal> dateRateMap, LocalDate date) {
    ExchangeRateId exchangeRateId = new ExchangeRateId(date, Currency.USD, Currency.JPY);
    BigDecimal rate = dateRateMap.get(exchangeRateId);
    if (rate == null) {
      ExchangeRate exchangeRate = exchangeRateService.getExchangeRate(exchangeRateId);
      rate = exchangeRate != null ? exchangeRate.getRate() : BigDecimal.ZERO;
      dateRateMap.put(exchangeRateId, rate);
    }
    return rate;
  }

  private AggregatedAnnualSaleDto generateAggregatedAnnualSaleDto(List<LotSaleDto> lotSaleDtoList) {
    AggregatedAnnualSaleDto aggregatedAnnualSaleDto = new AggregatedAnnualSaleDto();
    for (LotSaleDto lotSaleDto : lotSaleDtoList) {
      mergePriceMap(
          lotSaleDto.getTotalAcquisitionPrice(),
          aggregatedAnnualSaleDto.getTotalAcquisitionPrice());
      mergePriceMap(lotSaleDto.getTotalSoldPrice(), aggregatedAnnualSaleDto.getTotalSoldPrice());
      mergePriceMap(
          lotSaleDto.getTotalPriceDifference(), aggregatedAnnualSaleDto.getTotalRealizedGainLoss());
    }
    return aggregatedAnnualSaleDto;
  }

  /** Helper method to merge a price map into the corresponding map in aggregatedAnnualSaleDto. */
  private void mergePriceMap(
      Map<Currency, BigDecimal> sourceMap, Map<Currency, BigDecimal> targetMap) {
    sourceMap.forEach((currency, value) -> targetMap.merge(currency, value, BigDecimal::add));
  }
}
