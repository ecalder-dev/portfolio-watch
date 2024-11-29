package com.portfoliowatch.repository;

import com.portfoliowatch.model.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("SELECT MAX(t.dateTransacted) FROM Transaction t")
    Date findLatestDateTransacted();

}
