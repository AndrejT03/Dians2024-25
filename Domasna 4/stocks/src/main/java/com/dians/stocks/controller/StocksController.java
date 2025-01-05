package com.dians.stocks.controller;

import com.dians.stocks.domain.TechnicalIndicator;
import com.dians.stocks.dto.StockDTO;
import com.dians.stocks.dto.StockGraphDTO;
import com.dians.stocks.service.CompanyService;
import com.dians.stocks.service.StockDetailsService;
import com.dians.stocks.service.TechnicalIndicatorService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stocks")
public class StocksController {
  private final StockDetailsService stockDetailsService;
  private final CompanyService companyService;
  private final TechnicalIndicatorService technicalIndicatorService;

  public StocksController(StockDetailsService stockDetailsService, CompanyService companyService, TechnicalIndicatorService technicalIndicatorService) {
    this.stockDetailsService = stockDetailsService;
    this.companyService = companyService;
    this.technicalIndicatorService = technicalIndicatorService;
  }

  @GetMapping
  @Transactional
  public ResponseEntity<Map<String, Object>> getStocks(@RequestParam Long companyId,
                                                       @RequestParam int page,
                                                       @RequestParam int pageSize,
                                                       @RequestParam String sort) {
    Map<String, Object> response = new HashMap<>();
    Page<StockDTO> stocksPage = this.stockDetailsService.findAllStocksDTOByCompanyIdToPage(companyId, page, pageSize, sort);
    response.put("stocks", stocksPage.get().collect(Collectors.toList()));
    response.put("totalPageCount", stocksPage.getTotalPages());
    response.put("companyCode", this.companyService.findByIdToDTO(companyId).getCode());
    return ResponseEntity.ok().body(response);
  }

  @GetMapping("/graph")
  public ResponseEntity<List<StockGraphDTO>> getStocksForGraph(@RequestParam Long companyId,
                                                               @RequestParam Integer year) {
    return ResponseEntity.ok().body(this.stockDetailsService.findAllStockGraphDTOByCompanyIdAndYear(companyId, year));
  }

  @GetMapping("/graph-years")
  public ResponseEntity<List<Integer>> getYearsForGraph(@RequestParam Long companyId) {
    return ResponseEntity.ok().body(this.stockDetailsService.findGraphYearsAvailable(companyId));
  }

  @GetMapping("/technical-indicators")
  public ResponseEntity<List<TechnicalIndicator>> getTechnicalIndicators(@RequestParam Long companyId) {
    return ResponseEntity.ok().body(this.technicalIndicatorService.getTechnicalIndicators(companyId));
  }

  @GetMapping("/technical-oscillators")
  public ResponseEntity<List<TechnicalIndicator>> getTechnicalOscillators(@RequestParam Long companyId) {
    return ResponseEntity.ok().body(this.technicalIndicatorService.getTechnicalOscillators(companyId));
  }

  @GetMapping("/signals")
  public ResponseEntity<List<String>> getSignals(@RequestParam Long companyId) {
    return ResponseEntity.ok().body(this.technicalIndicatorService.getFinalSignalsList(companyId));
  }

}