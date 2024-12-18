package com.dians.stocks.controller;

import com.dians.stocks.dto.StockDTO;
import com.dians.stocks.service.CompanyService;
import com.dians.stocks.service.StockDetailsService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/table-data/stocks/")
public class StocksController {
  private final StockDetailsService stockDetailsService;
  private final CompanyService companyService;

  public StocksController(StockDetailsService stockDetailsService, CompanyService companyService) {
    this.stockDetailsService = stockDetailsService;
    this.companyService = companyService;
  }

  @GetMapping
  @Transactional
  public ResponseEntity<Map<String, Object>> getStocks(@RequestParam Long companyId,
                                                       @RequestParam int page,
                                                       @RequestParam int pageSize,
                                                       @RequestParam String sort) {
    Map<String, Object> response = new HashMap<>();
    Page<StockDTO> stocksPage = this.stockDetailsService.findRequestedStocks(companyId, page, pageSize, sort);
    response.put("stocks", stocksPage.get().collect(Collectors.toList()));
    response.put("company", this.companyService.findByIdToDTO(companyId));
    response.put("totalPageCount", stocksPage.getTotalPages());
    return ResponseEntity.ok().body(response);
  }
}
