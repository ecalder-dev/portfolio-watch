package com.portfoliowatch.repository;

import com.portfoliowatch.model.entity.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  // Custom method to find all records with a specific value for isHidden
  List<Account> findByIsHidden(boolean isHidden);
}
