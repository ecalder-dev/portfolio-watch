package com.portfoliowatch.repository.fx;

import com.portfoliowatch.model.entity.fx.ExchangeRate;
import com.portfoliowatch.model.entity.fx.ExchangeRateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, ExchangeRateId> {}
