package com.dians.stocks.controller;

import com.dians.stocks.domain.TechnicalIndicator;
import com.dians.stocks.helper_models.microservice.StockValues;
import com.dians.stocks.helper_models.dto.StockDTO;
import com.dians.stocks.helper_models.dto.StockGraphDTO;
import com.dians.stocks.service.CompanyService;
import com.dians.stocks.service.StockDetailsService;
import jakarta.transaction.Transactional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stocks")
public class StocksController {
  private final StockDetailsService stockDetailsService;
  private final CompanyService companyService;
  private final WebClient.Builder webClientBuilder;

  public StocksController(StockDetailsService stockDetailsService, CompanyService companyService, WebClient.Builder webClientBuilder) {
    this.stockDetailsService = stockDetailsService;
    this.companyService = companyService;
    this.webClientBuilder = webClientBuilder;
  }

  @GetMapping
  @Transactional
  // Returns a page of stocks based on the request parameters.
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
  // Returns a list of StockGraphDTO based on the request parameters.
  public ResponseEntity<List<StockGraphDTO>> getStocksForGraph(@RequestParam Long companyId,
                                                               @RequestParam Integer year) {
    return ResponseEntity.ok().body(this.stockDetailsService.findAllStockGraphDTOByCompanyIdAndYear(companyId, year));
  }

  @GetMapping("/graph-years")
  // Returns a list of all stock years available for a given company.
  public ResponseEntity<List<Integer>> getYearsForGraph(@RequestParam Long companyId) {
    return ResponseEntity.ok().body(this.stockDetailsService.findGraphYearsAvailable(companyId));
  }

  @GetMapping("/trend-indicators")
  /* Sends the latest 30 stocks for a given company id to technical-indicator
  * microservice (if they are available) in a POST request. Then the microservice
  * returns the trend indicators after finishing the calculations. */
  public ResponseEntity<List<TechnicalIndicator>> getTrendIndicators(@RequestParam Long companyId) {
    List<StockValues> stocks = this.stockDetailsService.findLast30ByCompanyId(companyId).reversed();

    List<TechnicalIndicator> trendList = webClientBuilder.build()
        .post()
        .uri("http://localhost:8082/api/technical-indicators/trend")
        .body(Mono.just(stocks), List.class)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<TechnicalIndicator>>() {})
        .block();

    if(trendList != null) {
      return ResponseEntity.ok().body(trendList);
    }

    return ResponseEntity.notFound().build();
  }

  @GetMapping("/momentum-indicators")
  /* Sends the latest 30 stocks for a given company id to technical-indicator
   * microservice (if they are available) in a POST request. Then the microservice
   * returns the momentum indicators after finishing the calculations. */
  public ResponseEntity<List<TechnicalIndicator>> getMomentumIndicators(@RequestParam Long companyId) {
    List<StockValues> stocks = this.stockDetailsService.findLast30ByCompanyId(companyId).reversed();

    List<TechnicalIndicator> momentumList = webClientBuilder.build()
        .post()
        .uri("http://localhost:8082/api/technical-indicators/momentum")
        .body(Mono.just(stocks), List.class)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<TechnicalIndicator>>() {})
        .block();

    if(momentumList != null) {
      return ResponseEntity.ok().body(momentumList);
    }

    return ResponseEntity.notFound().build();
  }

  @GetMapping("/signals")
  /* Sends the latest 30 stocks for a given company id to technical-indicator
   * microservice (if they are available) in a POST request. Then the microservice
   * returns the signals after finishing the calculations. */
  public ResponseEntity<List<String>> getSignals(@RequestParam Long companyId) {
    List<StockValues> stocks = this.stockDetailsService.findLast30ByCompanyId(companyId).reversed();

    List<String> signalsList = webClientBuilder.build()
        .post()
        .uri("http://localhost:8082/api/technical-indicators/signals")
        .body(Mono.just(stocks), List.class)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
        .block();

    if(signalsList != null) {
      return ResponseEntity.ok().body(signalsList);
    }

    return ResponseEntity.notFound().build();
  }

}