package com.portfoliowatch.repository;

import com.portfoliowatch.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    public List<Position> findAllByPortfolioId(Long id);
}
