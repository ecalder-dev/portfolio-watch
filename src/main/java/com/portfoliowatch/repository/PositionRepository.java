package com.portfoliowatch.repository;

import com.portfoliowatch.model.Position;
import com.portfoliowatch.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {



}