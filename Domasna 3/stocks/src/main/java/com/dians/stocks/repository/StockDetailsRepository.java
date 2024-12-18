package com.dians.stocks.repository;

import com.dians.stocks.domain.Company;
import com.dians.stocks.domain.StockDetailsHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StockDetailsRepository extends JpaRepository<StockDetailsHistory, Long> {
  Optional<StockDetailsHistory> findByDateAndCompany(LocalDate date, Company company);
  Page<StockDetailsHistory> findAllByCompanyId(Long companyId, Pageable pageable);
}
