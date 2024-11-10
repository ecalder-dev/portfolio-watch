package com.portfoliowatch.repository;

import com.portfoliowatch.model.entity.CorporateAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorporateActionRepository extends JpaRepository<CorporateAction, Long> {
    // Additional query methods can be defined here if needed
}
