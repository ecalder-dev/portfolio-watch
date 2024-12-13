package com.portfoliowatch.service;

import com.portfoliowatch.model.dto.AggregatedAnnualSaleDto;
import com.portfoliowatch.model.dto.LotSaleDto;
import com.portfoliowatch.model.entity.Lot;
import com.portfoliowatch.model.entity.LotSale;
import com.portfoliowatch.repository.LotSaleRepository;
import com.portfoliowatch.util.enums.LotSaleType;
import java.math.BigDecimal;
import java.util.Calendar;
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

  protected void recordLotSold(
      Lot lot,
      BigDecimal soldShares,
      BigDecimal soldPrice,
      Date dateTransacted,
      LotSaleType lotSaleType) {
    // Initialize LotSale object
    LotSale lotSale = new LotSale();
    String symbol = lot.getSymbol();
    BigDecimal purchasedPrice = lot.getPrice();
    BigDecimal totalPurchasedPrice = purchasedPrice.multiply(soldShares);
    BigDecimal totalSoldPrice = soldPrice.multiply(soldShares);
    BigDecimal totalPriceDifference = totalSoldPrice.subtract(totalPurchasedPrice);

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dateTransacted);
    Integer taxYear = calendar.get(Calendar.YEAR);

    // Set values in lotSale object
    lotSale.setSymbol(symbol);
    lotSale.setType(lotSaleType);
    lotSale.setSoldShares(soldShares);
    lotSale.setPurchasedPrice(purchasedPrice);
    lotSale.setTotalPurchasedPrice(totalPurchasedPrice);
    lotSale.setSoldPrice(soldPrice);
    lotSale.setTotalSoldPrice(totalSoldPrice);
    lotSale.setTotalPriceDifference(totalPriceDifference);
    lotSale.setTaxYear(taxYear);
    lotSale.setDateTransacted(dateTransacted);
    lotSale.setLot(lot);
    lotSale.setDatetimeCreated(new Date());
    lotSale.setDatetimeUpdated(lotSale.getDatetimeCreated());
    lotSaleRepository.save(lotSale);
  }

  private Map<String, BigDecimal> getSumTotalPriceDifferenceByTaxYear() {
    List<Object[]> results = lotSaleRepository.findSumTotalPriceDifferenceByTaxYear();
    Map<String, BigDecimal> taxYearMap = new HashMap<>();
    for (Object[] result : results) {
      String taxYear = (String) result[0];
      BigDecimal totalPriceDifference = (BigDecimal) result[1];
      taxYearMap.put(taxYear, totalPriceDifference);
    }
    return taxYearMap;
  }

  public AggregatedAnnualSaleDto getAggregatedLotSalesByYear(Integer year) {
    BigDecimal totalRealizedGainLoss = BigDecimal.ZERO;
    AggregatedAnnualSaleDto aggregatedAnnualSaleDto = new AggregatedAnnualSaleDto();
    List<LotSaleDto> lotSaleDtoList = lotSaleRepository.findAggregatedLotSalesByYear(year);
    for (LotSaleDto lotSaleDto : lotSaleDtoList) {
      totalRealizedGainLoss = totalRealizedGainLoss.add(lotSaleDto.getTotalPriceDifference());
    }
    aggregatedAnnualSaleDto.setYear(year);
    aggregatedAnnualSaleDto.setTotalRealizedGainLoss(totalRealizedGainLoss);
    aggregatedAnnualSaleDto.setTotalRealizedGainLossYen(
        totalRealizedGainLoss.multiply(BigDecimal.ZERO));
    aggregatedAnnualSaleDto.setSoldLots(lotSaleDtoList);
    return aggregatedAnnualSaleDto;
  }

  protected void deleteAll() {
    lotSaleRepository.deleteAll();
  }
}
