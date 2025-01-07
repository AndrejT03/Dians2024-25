package com.dians.stocks.service;

import com.dians.stocks.domain.TechnicalIndicator;

import java.util.List;

public interface TechnicalIndicatorService {
    /* Short explanation for the methods that need one is located
    * above the method implementations in the service implementation. */
    List<TechnicalIndicator> getTrendIndicators(Long companyId);
    List<TechnicalIndicator> getMomentumIndicators(Long companyId);
    String getSignalFromTrendIndicatorsByTimeframe(double currentPrice, double smaValue, double emaValue, double hmaValue, double vwmaValue, double ichimokuBaselineValue);
    String getFinalSignalByTimeframe(int timeframe, int numberOfStocksAvailable, List<TechnicalIndicator> indicatorsList, List<TechnicalIndicator> oscillatorsList);
    List<String> getFinalSignalsList(Long companyId);
}