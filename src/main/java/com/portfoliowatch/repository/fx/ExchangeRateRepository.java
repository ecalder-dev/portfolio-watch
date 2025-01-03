package com.portfoliowatch.repository.fx;

import com.portfoliowatch.model.entity.fx.ExchangeRate;
import com.portfoliowatch.model.entity.fx.ExchangeRateId;
import com.portfoliowatch.util.enums.Currency;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, ExchangeRateId> {

  @Query(
      """
        SELECT e FROM ExchangeRate e
        WHERE e.exchangeRateId.fromCurrency = :fromCurrency
          AND e.exchangeRateId.toCurrency = :toCurrency
        """)
  List<ExchangeRate> findAllByCurrency(
      @Param("fromCurrency") Currency fromCurrency, @Param("toCurrency") Currency toCurrency);

  @Query(
      """
    SELECT e FROM ExchangeRate e
    WHERE e.exchangeRateId.fromCurrency = :fromCurrency
      AND e.exchangeRateId.toCurrency = :toCurrency
      AND e.exchangeRateId.date BETWEEN :startDate AND :endDate
    """)
  List<ExchangeRate> findAllByCurrencyAndDateRange(
      @Param("fromCurrency") Currency fromCurrency,
      @Param("toCurrency") Currency toCurrency,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query(
      """
          SELECT e FROM ExchangeRate e
          WHERE e.exchangeRateId.fromCurrency = :fromCurrency
            AND e.exchangeRateId.toCurrency = :toCurrency
            AND e.exchangeRateId.date <= :date
      """)
  List<ExchangeRate> findAllByCurrencyDateRangeBeforeInclusive(
      @Param("fromCurrency") Currency fromCurrency,
      @Param("toCurrency") Currency toCurrency,
      @Param("date") LocalDate date);

  @Query(
      """
        SELECT e FROM ExchangeRate e
        WHERE e.exchangeRateId.fromCurrency = :fromCurrency
          AND e.exchangeRateId.toCurrency = :toCurrency
          AND e.exchangeRateId.date >= :date
    """)
  List<ExchangeRate> findAllByCurrencyDateRangeAfterInclusive(
      @Param("fromCurrency") Currency fromCurrency,
      @Param("toCurrency") Currency toCurrency,
      @Param("date") LocalDate date);
}
