package com.dians.stocks.service;

import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.domain.TechnicalIndicator;

import java.math.BigDecimal;
import java.util.List;

public interface TechnicalIndicatorService {
    /* Short explanation for the methods that need one is located
     * above the method implementations in the service implementation. */
    List<TechnicalIndicator> getTechnicalIndicators(Long companyId);
    List<TechnicalIndicator> getTechnicalOscillators(Long companyId);
    void setValuesForIndicator(TechnicalIndicator indicator);
    List<StockDetailsHistory> getLastNStocks(int count); // done
    String getOscillatorSignal(String code, double value);
    List<BigDecimal> getLastNValues(List<BigDecimal> list, int count); // done
    void checkIfIssuerHasEnoughStocksForTechnicalIndicator(TechnicalIndicator indicator); //
    double getHighestHighForStocks(List<StockDetailsHistory> stocks); //
    double getLowestLowForStocks(List<StockDetailsHistory> stocks); //
    BigDecimal calculateIndicatorValueByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks, String code);
    double calculateAverageValueForRSI(List<BigDecimal> list); //
    void calculateGainsAndLossesForRSI(List<BigDecimal> gains, List<BigDecimal> losses); //
    double calculateWMA(List<Double> stockPrices, int period); //
    BigDecimal calculateRSIValuesByTimeframe(boolean hasEnoughData, int numberOfDays);
    double calculateMeanDeviationForCCI(List<Double> typicalPrices, double SMA); //
    List<Double> calculateTypicalPricesForGivenStocks(List<StockDetailsHistory> stocks); //
    double calculateSMAForGivenPrices(List<Double> prices); //
    String getSignalFromIndicatorsByTimeframe(double currentPrice, double smaValue, double emaValue, double hmaValue, double vwmaValue, double ichimokuBaselineValue);
    String getFinalSignalByTimeframe(int timeframe, int numberOfStocksAvailable, List<TechnicalIndicator> indicatorsList, List<TechnicalIndicator> oscillatorsList);
    List<String> getFinalSignalsList(Long companyId);
}