package com.dians.stocks.service;

import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.domain.TechnicalIndicator;

import java.math.BigDecimal;
import java.util.List;

public interface TechnicalIndicatorService {
    List<TechnicalIndicator> getTechnicalIndicators(Long companyId);
    List<TechnicalIndicator> getTechnicalOscillators(Long companyId);
    List<StockDetailsHistory> getLastNStocks(int count);
    List<BigDecimal> getLastNValues(List<BigDecimal> list, int count);
    void checkIfIssuerHasEnoughStocksForTechnicalIndicator(TechnicalIndicator indicator);
    TechnicalIndicator calculateRSI();
    TechnicalIndicator calculateROC();
    TechnicalIndicator calculateWPR();
    TechnicalIndicator calculateSTO();
    TechnicalIndicator calculateCCI();
    TechnicalIndicator calculateSMA();
    TechnicalIndicator calculateEMA();
    TechnicalIndicator calculateVWMA();
    TechnicalIndicator calculateHMA();
    TechnicalIndicator calculateIBL();
    String getRSISignal(double value);
    String getROCSignal(double value);
    String getWPRSignal(double value);
    String getSTOSignal(double value);
    String getCCISignal(double value);
    double calculateAverageValueForRSI(List<BigDecimal> list);
    void calculateGainsAndLossesForRSI(List<BigDecimal> gains, List<BigDecimal> losses);
    double calculateWMA(List<Double> stockPrices, int period);
    BigDecimal calculateRSIValuesByTimeframe(int numberOfDays, boolean hasEnoughData, List<BigDecimal> gains, List<BigDecimal> losses);
    BigDecimal calculateROCValuesByTimeframe(int numberOfDays, boolean hasEnoughData);
    BigDecimal calculateWPRValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks);
    BigDecimal calculateSTOValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks);
    BigDecimal calculateCCIValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks);
    BigDecimal calculateSMAValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks);
    BigDecimal calculateEMAValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks);
    BigDecimal calculateVWMAValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks);
    BigDecimal calculateHMAValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks);
    BigDecimal calculateIBLValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks);
    double calculateMeanDeviationForCCI(List<Double> typicalPrices, double SMA);
    List<Double> calculateTypicalPricesForGivenStocks(List<StockDetailsHistory> stocks);
    double calculateSMAForGivenPrices(List<Double> prices);
    String getSignalFromIndicatorsByTimeframe(double currentPrice, double smaValue, double emaValue, double hmaValue, double vwmaValue, double ichimokuBaselineValue);
    String getFinalSignalByTimeframe(int timeframe, int numberOfStocksAvailable, List<TechnicalIndicator> indicatorsList, List<TechnicalIndicator> oscillatorsList);
    List<String> getFinalSignalsList(Long companyId);
}