package com.dians.stocks.service;

import com.dians.stocks.domain.Company;
import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.dto.StockDTO;
import com.dians.stocks.dto.StockGraphDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockDetailsService {
  /* The explanation of the few confusing names are above
   * the method overrides in the service implementation. */
  Optional<StockDetailsHistory> findByDateAndCompany(LocalDate date, Company company);
  Page<StockDTO> findAllStocksDTOByCompanyIdToPage(Long companyId, int page, int pageSize, String sort);
  void addStockDetailToCompany(Long companyId, StockDetailsHistory stockDetailsHistory);
  List<StockGraphDTO> findAllStockGraphDTOByCompanyIdAndYear(Long companyId, Integer year);
  List<Integer> findGraphYearsAvailable(Long companyId);
  List<StockDetailsHistory> findLast30ByCompanyId(Long companyId);
}