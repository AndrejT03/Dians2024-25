package com.dians.stocks.service;

import com.dians.stocks.domain.Company;
import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.dto.StockDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Optional;

public interface StockDetailsService {
  Optional<StockDetailsHistory> findByDateAndCompany(LocalDate date, Company company);
  Page<StockDTO> findRequestedStocks(Long companyId, int page, int pageSize, String sort);
  void addStockDetailToCompany(Long companyId, StockDetailsHistory stockDetailsHistory);
  StockDTO convertToStockDTO(StockDetailsHistory stock);
}
