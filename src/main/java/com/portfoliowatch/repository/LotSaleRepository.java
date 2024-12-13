package com.portfoliowatch.repository;

import com.portfoliowatch.model.dto.LotSaleDto;
import com.portfoliowatch.model.entity.LotSale;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LotSaleRepository extends JpaRepository<LotSale, UUID> {

  @Query(
      "SELECT ls.taxYear, SUM(ls.totalPriceDifference) "
          + "FROM LotSale ls "
          + "GROUP BY ls.taxYear")
  List<Object[]> findSumTotalPriceDifferenceByTaxYear();

  @Query(
      "SELECT new com.portfoliowatch.model.dto.LotSaleDto(ls.symbol, ls.dateTransacted, "
          + "SUM(ls.totalPurchasedPrice),"
          + "SUM(ls.totalSoldPrice), "
          + "SUM(ls.totalPriceDifference)) "
          + "FROM LotSale ls "
          + "WHERE ls.taxYear = :year "
          + "GROUP BY ls.symbol, ls.dateTransacted")
  List<LotSaleDto> findAggregatedLotSalesByYear(@Param("year") Integer year);
}
