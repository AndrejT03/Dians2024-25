package com.dians.stocks.service;

import com.dians.stocks.domain.Company;
import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.dto.StockDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockDetailsService {
  Optional<StockDetailsHistory> findByDateAndCompany(LocalDate date, Company company);
  Optional<StockDetailsHistory> save(StockDetailsHistory stockDetails);
  void addStockDetailToCompany(Long companyId, StockDetailsHistory stockDetailsHistory);
  StockDTO convertToStockDTO(StockDetailsHistory stock);
  List<StockDTO> findRequestedStocks(Long companyId, int page, int pageSize, String sort);
  int countStocks(Long companyId);
}
