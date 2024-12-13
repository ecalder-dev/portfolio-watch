package com.portfoliowatch.repository;

import com.portfoliowatch.model.entity.Transfer;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

  @Query("SELECT MAX(t.datetimeUpdated) FROM Transfer t")
  Date findLatestDatetimeUpdated();
}
