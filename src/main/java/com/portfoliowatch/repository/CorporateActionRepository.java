package com.portfoliowatch.repository;

import com.portfoliowatch.model.entity.CorporateAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CorporateActionRepository extends JpaRepository<CorporateAction, Long> {

    @Query("SELECT MAX(ca.datetimeUpdated) FROM CorporateAction ca")
    Date findLatestDatetimeUpdated();

}
