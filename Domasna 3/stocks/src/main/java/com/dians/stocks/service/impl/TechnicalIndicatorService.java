package com.dians.stocks.service;

import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.domain.TechnicalIndicator;

import java.math.BigDecimal;
import java.util.List;

public interface TechnicalIndicatorService {
    TechnicalIndicator calculateRSI();
    TechnicalIndicator calculateROC();
    TechnicalIndicator calculateWPR();
    TechnicalIndicator calculateSMA();
    TechnicalIndicator calculateEMA();
    List<TechnicalIndicator> getTechnicalIndicators(Long companyId);
    List<StockDetailsHistory> getLastNStocks(int count);
    List<BigDecimal> getLastNValues(List<BigDecimal> list, int count);
    double calculateAverageValueForRSI(List<BigDecimal> list);
    void calculateGainsAndLossesForRSI(List<BigDecimal> gains, List<BigDecimal> losses);
    BigDecimal calculateRSIValuesByTimeframe(int numberOfDays, boolean hasEnoughData, List<BigDecimal> gains, List<BigDecimal> losses);
    BigDecimal calculateROCValuesByTimeframe(int numberOfDays, boolean hasEnoughData);
    BigDecimal calculateWPRValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks);
    BigDecimal calculateSMAValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks);
    BigDecimal calculateEMAValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks);
    void checkIfIssuerHasEnoughStocksForTechnicalIndicator(TechnicalIndicator indicator);
}