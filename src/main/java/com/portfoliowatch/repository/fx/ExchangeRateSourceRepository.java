package com.portfoliowatch.repository.fx;

import com.portfoliowatch.model.entity.fx.ExchangeRateSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateSourceRepository extends JpaRepository<ExchangeRateSource, Long> {}
