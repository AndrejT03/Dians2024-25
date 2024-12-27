package com.dians.stocks.service.impl;

import com.dians.stocks.domain.Company;
import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.dto.StockDTO;
import com.dians.stocks.dto.StockGraphDTO;
import com.dians.stocks.repository.CompanyRepository;
import com.dians.stocks.repository.StockDetailsRepository;
import com.dians.stocks.service.StockDetailsService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class StockDetailsServiceImpl implements StockDetailsService {
  private final StockDetailsRepository stockDetailsRepository;
  private final CompanyRepository companyRepository;

  public StockDetailsServiceImpl(StockDetailsRepository stockDetailsRepository, CompanyRepository companyRepository) {
    this.stockDetailsRepository = stockDetailsRepository;
    this.companyRepository = companyRepository;
  }

  @Override
  public Optional<StockDetailsHistory> findByDateAndCompany(LocalDate date, Company company) {
    return this.stockDetailsRepository.findByDateAndCompany(date, company);
  }

  @Override
  @Transactional
  public void addStockDetailToCompany(Long companyId, StockDetailsHistory stockDetailsHistory) {
    Company company = this.companyRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not found!"));
    stockDetailsHistory.setCompany(company);
    company.getStockHistory().add(stockDetailsHistory);
    company.updateStockInfo(stockDetailsHistory.getDateAsString());
    this.stockDetailsRepository.save(stockDetailsHistory);
  }

  @Override
  @Transactional
  public StockDTO convertToStockDTO(StockDetailsHistory stock) {
    return new StockDTO()
            .builder()
            .date(stock.getDateAsString())
            .originalDate(stock.getDate())
            .lastTransactionPrice(stock.getPriceFormatted(stock.getLastTransactionPrice()))
            .minPrice(stock.getPriceFormatted(stock.getMinPrice()))
            .maxPrice(stock.getPriceFormatted(stock.getMinPrice()))
            .averagePrice(stock.getPriceFormatted(stock.getAveragePercentage()))
            .averagePercentage(stock.getPriceFormatted(stock.getAveragePercentage()))
            .quantity(stock.getQuantity())
            .turnoverInBestDenars(stock.getPriceFormatted(stock.getTurnoverInBestDenars()))
            .totalTurnoverInDenars(stock.getPriceFormatted(stock.getTotalTurnoverInDenars()))
            .build();
  }

  @Override
  public StockGraphDTO convertToStockGraphDTO(StockDetailsHistory stock) {
    return new StockGraphDTO()
            .builder()
            .date(stock.getDate())
            .price(stock.getLastTransactionPrice())
            .build();
  }

  @Override
  public List<StockGraphDTO> findAllStockGraphDTOByCompanyIdAndYear(Long companyId, Integer year) {
    LocalDate startDate = LocalDate.of(year, 1, 1);
    LocalDate endDate = LocalDate.of(year, 12, 31);
    return this.stockDetailsRepository.findAllByDateBetweenAndCompanyId(startDate, endDate, companyId)
            .stream()
            .map(this::convertToStockGraphDTO).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public List<Integer> findGraphYearsAvailable(Long companyId) {
    Set<Integer> yearSet = new TreeSet<>();
    this.stockDetailsRepository.findAllByCompanyId(companyId)
            .forEach(stock -> {
              yearSet.add(Integer.parseInt(stock.getDateAsString().split("\\.")[2]));
            });
    return yearSet.stream().toList().reversed();
  }

  @Override
  public List<StockDetailsHistory> findLast30ByCompanyId(Long companyId) {
    Pageable pageable = PageRequest.of(0, 30, Sort.by("date").descending());
    return this.stockDetailsRepository.findAllByCompanyId(companyId, pageable).stream().toList();
  }

  @Override
  @Transactional
  public Page<StockDTO> findRequestedStocks(Long companyId, int page, int pageSize, String sort) {
    String sortBy = sort.split("-")[0];
    String order = sort.split("-")[1];

    Pageable pageable;
    if(order.equals("asc")) {
      pageable = PageRequest.of(page, pageSize, Sort.by(sortBy).ascending());
    }
    else {
      pageable = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
    }

    Page<StockDetailsHistory> stocksPage = this.stockDetailsRepository.findAllByCompanyId(companyId, pageable);
    List<StockDTO> stocksList = stocksPage
            .stream()
            .map(this::convertToStockDTO).collect(Collectors.toList());

    return new PageImpl<>(stocksList, stocksPage.getPageable(), stocksPage.getTotalElements());
  }

}
