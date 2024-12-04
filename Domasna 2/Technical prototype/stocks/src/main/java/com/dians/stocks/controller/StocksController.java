package com.dians.stocks.controller;

import com.dians.stocks.service.CompanyService;
import com.dians.stocks.service.StockDetailsService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
  public ResponseEntity<Map<String, Object>> getStocks(@RequestParam String companyId,
                                                       @RequestParam int page,
                                                       @RequestParam int pageSize,
                                                       @RequestParam String sort) {
    Map<String, Object> response = new HashMap<>();
    Long id = Long.parseLong(companyId);
    int totalNumberOfPages = (int) Math.ceil((double) this.stockDetailsService.countStocks(id) / pageSize);
    response.put("stocks", this.stockDetailsService.findRequestedStocks(id, page, pageSize, sort));
    response.put("company", this.companyService.findByIdToDTO(id));
    response.put("totalCount", totalNumberOfPages);
    return ResponseEntity.ok().body(response);
  }
}
