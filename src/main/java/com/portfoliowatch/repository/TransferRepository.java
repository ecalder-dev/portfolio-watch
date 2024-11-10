package com.portfoliowatch.repository;

import com.portfoliowatch.model.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    // Additional query methods can be defined here if needed
}
