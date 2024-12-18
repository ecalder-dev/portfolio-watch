package com.portfoliowatch.repository;

import com.portfoliowatch.model.entity.LotSale;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LotSaleRepository extends JpaRepository<LotSale, UUID> {

  @Query("SELECT ls.taxYear, SUM(ls.totalPriceDifference) FROM LotSale ls GROUP BY ls.taxYear")
  List<Object[]> findSumTotalPriceDifferenceByTaxYear();

  @Query(
      "SELECT DISTINCT(ls.taxYear) FROM LotSale ls  GROUP BY ls.taxYear ORDER BY ls.taxYear ASC ")
  List<Integer> findAllDistinctTaxYear();

  List<LotSale> findLotSalesByTaxYearOrderByDateSoldAsc(Integer year);
}
