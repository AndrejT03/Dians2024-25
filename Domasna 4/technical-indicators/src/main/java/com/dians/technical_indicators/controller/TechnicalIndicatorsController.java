package com.dians.technical_indicators.controller;

import com.dians.technical_indicators.domain.StockValues;
import com.dians.technical_indicators.domain.TechnicalIndicator;
import com.dians.technical_indicators.service.TechnicalIndicatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technical-indicators")
public class TechnicalIndicatorsController {
  private final TechnicalIndicatorService technicalIndicatorService;

  public TechnicalIndicatorsController(TechnicalIndicatorService technicalIndicatorService) {
    this.technicalIndicatorService = technicalIndicatorService;
  }

  @PostMapping("/trend")
  /* Returns a list of the trend indicators
  * based on the stocks from the POST request. */
  public ResponseEntity<List<TechnicalIndicator>> getTrendIndicators(@RequestBody List<StockValues> stocks) {
    this.technicalIndicatorService.setStocks(stocks);
    return ResponseEntity.ok().body(this.technicalIndicatorService.getTrendIndicators());
  }

  @PostMapping("/momentum")
  /* Returns a list of the momentum indicators based
  * on the stocks from the POST request. */
  public ResponseEntity<List<TechnicalIndicator>> getMomentumIndicators(@RequestBody List<StockValues> stocks) {
    this.technicalIndicatorService.setStocks(stocks);
    return ResponseEntity.ok().body(this.technicalIndicatorService.getMomentumIndicators());
  }

  @PostMapping("/signals")
  /* Returns the signals based on the technical indicators
  * calculated in the background based on the stocks from the POST request.*/
  public ResponseEntity<List<String>> getSignals(@RequestBody List<StockValues> stocks) {
    this.technicalIndicatorService.setStocks(stocks);
    return ResponseEntity.ok().body(this.technicalIndicatorService.getFinalSignalsList());
  }

}
