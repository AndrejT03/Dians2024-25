package com.dians.stocks.service.impl;

import com.dians.stocks.domain.Company;
import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.dto.StockDTO;
import com.dians.stocks.repository.CompanyRepository;
import com.dians.stocks.repository.StockDetailsRepository;
import com.dians.stocks.service.StockDetailsService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
  public Optional<StockDetailsHistory> save(StockDetailsHistory stockDetails) {
    return Optional.of(this.stockDetailsRepository.save(stockDetails));
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
  @Transactional
  public List<StockDTO> findRequestedStocks(Long companyId, int page, int pageSize, String sort) {
    List<StockDTO> stocks = this.stockDetailsRepository.findAll()
        .stream().filter(s -> s.getCompany().getId().equals(companyId))
        .map(this::convertToStockDTO).collect(Collectors.toList());

    if(sort.equals("date-closest")) {
      stocks.sort(Comparator.comparing(StockDTO::getOriginalDate).reversed());
    }
    else {
      stocks.sort(Comparator.comparing(StockDTO::getOriginalDate));
    }

    return stocks.stream().skip((long) page * pageSize).limit(pageSize).collect(Collectors.toList());
  }

  @Override
  public int countStocks(Long companyId) {
    return (int) this.stockDetailsRepository.findAll()
        .stream().filter(s -> s.getCompany().getId().equals(companyId)).count();
  }


}
