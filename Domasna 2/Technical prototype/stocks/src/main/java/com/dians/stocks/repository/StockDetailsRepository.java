package com.dians.stocks.repository;

import com.dians.stocks.domain.Company;
import com.dians.stocks.domain.StockDetailsHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockDetailsRepository extends JpaRepository<StockDetailsHistory, Long> {
  Optional<StockDetailsHistory> findByDateAndCompany(LocalDate date, Company company);
}
