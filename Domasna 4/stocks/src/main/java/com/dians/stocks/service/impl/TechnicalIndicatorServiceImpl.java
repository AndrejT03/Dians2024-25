package com.dians.stocks.service.impl;

import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.domain.TechnicalIndicator;
import com.dians.stocks.service.StockDetailsService;
import com.dians.stocks.service.TechnicalIndicatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TechnicalIndicatorServiceImpl implements TechnicalIndicatorService {
    private final StockDetailsService stockDetailsService;
    private List<StockDetailsHistory> stocks;

    public TechnicalIndicatorServiceImpl(StockDetailsService stockDetailsService) {
        this.stockDetailsService = stockDetailsService;
    }

    @Override
    /* Creates 5 indicators, sets their values and returns them as a list. */
    public List<TechnicalIndicator> getTechnicalIndicators(Long companyId) {
        this.stocks = this.stockDetailsService.findLast30ByCompanyId(companyId).reversed();
        List<TechnicalIndicator> indicatorsList = new ArrayList<>();

        indicatorsList.add(new TechnicalIndicator("SMA", "Simple Moving Average"));
        indicatorsList.add(new TechnicalIndicator("EMA", "Exponential Moving Average"));
        indicatorsList.add(new TechnicalIndicator("HMA", "Hull Moving Average"));
        indicatorsList.add(new TechnicalIndicator("VWMA", "Volume Weighted Moving Average"));
        indicatorsList.add(new TechnicalIndicator("IBL", "Ichimoku Baseline"));

        for(TechnicalIndicator indicator : indicatorsList) {
            setValuesForIndicator(indicator);
        }

        return indicatorsList;
    }

    @Override
    /* Creates 5 oscillators, sets their values and returns them as a list. */
    public List<TechnicalIndicator> getTechnicalOscillators(Long companyId) {
        this.stocks = this.stockDetailsService.findLast30ByCompanyId(companyId).reversed();
        List<TechnicalIndicator> oscillatorsList = new ArrayList<>();

        oscillatorsList.add(new TechnicalIndicator("RSI", "Relative Strength Index"));
        oscillatorsList.add(new TechnicalIndicator("ROC", "Rate of Change"));
        oscillatorsList.add(new TechnicalIndicator("%R", "Williams Percent Range"));
        oscillatorsList.add(new TechnicalIndicator("%K", "Stochastic Oscillator"));
        oscillatorsList.add(new TechnicalIndicator("CCI", "Commodity Channel Index"));

        for(TechnicalIndicator oscillator : oscillatorsList) {
            setValuesForIndicator(oscillator);
            oscillator.setShortTermSignal(getOscillatorSignal(oscillator.code, oscillator.getValueByWeek().doubleValue()));
            oscillator.setLongTermSignal(getOscillatorSignal(oscillator.code, oscillator.getValueByMonth().doubleValue()));
        }

        return oscillatorsList;
    }

    @Override
    /* Sets the values for the indicator/oscillator that calls the method to eliminate the duplicate code. */
    public void setValuesForIndicator(TechnicalIndicator indicator) {
        checkIfIssuerHasEnoughStocksForTechnicalIndicator(indicator);

        if(!indicator.code.equals("RSI")) {
            indicator.setValueByDay(calculateIndicatorValueByTimeframe(indicator.isDayDataEnough(), getLastNStocks(2), indicator.code));
            indicator.setValueByWeek(calculateIndicatorValueByTimeframe(indicator.isWeekDataEnough(), getLastNStocks(7), indicator.code));
            indicator.setValueByMonth(calculateIndicatorValueByTimeframe(indicator.isMonthDataEnough(), this.stocks, indicator.code));
        }
        else {
            indicator.setValueByDay(calculateRSIValuesByTimeframe(indicator.isDayDataEnough(), 2));
            indicator.setValueByWeek(calculateRSIValuesByTimeframe(indicator.isWeekDataEnough(), 7));
            indicator.setValueByMonth(calculateRSIValuesByTimeframe(indicator.isMonthDataEnough(), 30));
        }
    }

    @Override
    public List<StockDetailsHistory> getLastNStocks(int count) {
        if (this.stocks == null || this.stocks.isEmpty() || count <= 1) {
            return new ArrayList<>();
        }

        int size = this.stocks.size();
        return this.stocks.subList(Math.max(size - count, 0), size);
    }

    @Override
    /* Returns the last 'count' elements from the given list. */
    public List<BigDecimal> getLastNValues(List<BigDecimal> list, int count) {
        int size = list.size();
        return list.subList(Math.max(size - count, 0), size);
    }

    @Override
    /* Checks if a stock has enough instances for calculating the indicator value */
    public void checkIfIssuerHasEnoughStocksForTechnicalIndicator(TechnicalIndicator indicator) {
        if(this.stocks.size() < 2) {
            indicator.setDayDataEnough(false);
            indicator.setWeekDataEnough(false);
            indicator.setMonthDataEnough(false);
        }
        else if(this.stocks.size() < 7) {
            indicator.setDayDataEnough(true);
            indicator.setWeekDataEnough(false);
            indicator.setMonthDataEnough(false);
        }
        else if(this.stocks.size() < 30) {
            indicator.setDayDataEnough(true);
            indicator.setWeekDataEnough(true);
            indicator.setMonthDataEnough(false);
        }
        else {
            indicator.setDayDataEnough(true);
            indicator.setWeekDataEnough(true);
            indicator.setMonthDataEnough(true);
        }
    }

    @Override
    /* Returns the highest maximum price for the given stocks. */
    public double getHighestHighForStocks(List<StockDetailsHistory> stocks) {
        double highestHigh = stocks.getFirst().getMaxPrice().doubleValue();

        for(int i=1; i<stocks.size(); i++) {
            highestHigh = Math.max(highestHigh, stocks.get(i).getMaxPrice().doubleValue());
        }
        return highestHigh;
    }

    @Override
    /* Returns the lowest minimum price for the given stocks. */
    public double getLowestLowForStocks(List<StockDetailsHistory> stocks) {
        double lowestLow = stocks.getFirst().getMinPrice().doubleValue();

        for(int i=1; i<stocks.size(); i++) {
            lowestLow = Math.min(lowestLow, stocks.get(i).getMinPrice().doubleValue());
        }
        return lowestLow;
    }

    @Override
    /* This method has a lot of code, but it saves us from the duplicate code by calculating
     * the indicator value depending on the code of the indicator that is calling the method. */
    public BigDecimal calculateIndicatorValueByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks, String code) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

        double result = 0;
        /* Inside this switch-case we have implemented calculation
         * of the value based on the formula for each indicator. */
        switch(code) {
            case "ROC": {
                double currentPrice = stocks.getLast().getLastTransactionPrice().doubleValue();
                double previousPrice = stocks.getFirst().getLastTransactionPrice().doubleValue();
                result = (currentPrice - previousPrice) / previousPrice * 100;
            } break;
            case "%R": {
                double close = stocks.getLast().getLastTransactionPrice().doubleValue();
                double highestHigh = getHighestHighForStocks(stocks);
                double lowestLow = getLowestLowForStocks(stocks);

                if(highestHigh == lowestLow) {
                    return new BigDecimal(0);
                }

                result = (highestHigh - close) / (highestHigh - lowestLow) * -100;
            } break;
            case "%K": {
                double currentClose = stocks.getLast().getLastTransactionPrice().doubleValue();
                double highestHigh = getHighestHighForStocks(stocks);
                double lowestLow = getLowestLowForStocks(stocks);

                if(highestHigh == lowestLow) {
                    return new BigDecimal(0);
                }

                result = (currentClose - lowestLow) / (highestHigh - lowestLow) * 100;
            } break;
            case "CCI": {
                List<Double> typicalPrices = calculateTypicalPricesForGivenStocks(stocks);
                double SMA = calculateSMAForGivenPrices(typicalPrices);
                double meanDeviation = calculateMeanDeviationForCCI(typicalPrices, SMA);

                if(meanDeviation == 0) {
                    return new BigDecimal(0);
                }

                double lastTP = typicalPrices.getLast();

                result = (lastTP - SMA) / (0.015 * meanDeviation);
            } break;
            case "SMA": {
                List<Double> prices = stocks.stream()
                        .map(StockDetailsHistory::getLastTransactionPrice)
                        .mapToDouble(BigDecimal::doubleValue)
                        .boxed()
                        .toList();
                result = calculateSMAForGivenPrices(prices);
            } break;
            case "EMA": {
                int size = stocks.size();
                double k = 2.0 / (size + 1);
                double EMA = stocks.getFirst().getLastTransactionPrice().doubleValue();
                for(StockDetailsHistory stock : stocks) {
                    EMA = stock.getLastTransactionPrice().doubleValue() * k + (EMA * (1 - k));
                }
                result = EMA;
            } break;
            case "VWMA": {
                double totalWeightedPrice = 0;
                double totalVolume = 0;

                for(StockDetailsHistory stock : stocks) {
                    totalWeightedPrice += (stock.getLastTransactionPrice().doubleValue() * stock.getQuantity());
                    totalVolume += stock.getQuantity();
                }

                result = totalVolume != 0 ? totalWeightedPrice / totalVolume : 0;
            } break;
            case "HMA": {
                List<Double> stockPrices = stocks.stream().map(s -> s.getLastTransactionPrice().doubleValue()).toList();
                List<Double> rawHMAList = new ArrayList<>();
                List<Double> hmaList = new ArrayList<>();

                int period = 9;
                int halfPeriod = (int) (period / 2.0);
                int sqrtPeriod = (int) Math.sqrt(period);

                for(int i=0; i<stockPrices.size(); i++) {
                    List<Double> pricesSubList = stockPrices.subList(0, i + 1);

                    double halfWMA = calculateWMA(pricesSubList, halfPeriod);
                    double fullWMA = calculateWMA(pricesSubList, period);
                    double rawHMA = 2 * halfWMA - fullWMA;

                    rawHMAList.add(rawHMA);

                    double hma = calculateWMA(rawHMAList, sqrtPeriod);
                    hmaList.add(hma);
                }

                result = hmaList.getLast();
            } break;
            case "IBL": {
                double highestHigh = getHighestHighForStocks(stocks);
                double lowestLow = getLowestLowForStocks(stocks);

                result = (highestHigh + lowestLow) / 2;
            } break;
            default: break;
        }

        // We are returning a BigDecimal object as the result with the decimals rounded on 2 places.
        return new BigDecimal(result).setScale(2, RoundingMode.DOWN);
    }

    @Override
    /* Returns the signal for the oscillator calling the method based on his code. */
    public String getOscillatorSignal(String code, double value) {
        String signal = "";
        /* Every oscillator has different signal calculation */
        switch (code) {
            case "RSI": {
                if(value < 30) {
                    signal = "Buy";
                }
                else if(value > 70) {
                    signal = "Sell";
                }
                else {
                    signal = "Hold";
                }
            } break;
            case "ROC": {
                if(value < -5) {
                    signal = "Sell";
                }
                else if(value > 5) {
                    signal = "Buy";
                }
                else {
                    signal = "Hold";
                }
            } break;
            case "%R": {
                if(value < -80) {
                    signal = "Buy";
                }
                else if(value > -20) {
                    signal = "Sell";
                }
                else {
                    signal = "Hold";
                }
            } break;
            case "%K": {
                if(value < 20) {
                    signal = "Buy";
                }
                else if(value > 80) {
                    signal = "Sell";
                }
                else {
                    signal = "Hold";
                }
            } break;
            case "CCI": {
                if(value > 100) {
                    signal = "Buy";
                }
                else if(value < -100) {
                    signal = "Sell";
                }
                else {
                    signal = "Hold";
                }
            } break;
            default: break;
        }

        return signal;
    }

    @Override
    /* Calculates the RSI value and returns it. Only RSI has its own method
     * since it has a more complex way of calculating and needs more helper methods. */
    public BigDecimal calculateRSIValuesByTimeframe(boolean hasEnoughData, int numberOfDays) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

        List<BigDecimal> gains = new ArrayList<>();
        List<BigDecimal> losses = new ArrayList<>();

        calculateGainsAndLossesForRSI(gains, losses);

        double averageGains = calculateAverageValueForRSI(getLastNValues(gains, numberOfDays));
        double averageLosses = calculateAverageValueForRSI(getLastNValues(losses, numberOfDays));

        if(averageGains == 0 && averageLosses == 0) {
            return new BigDecimal(50);
        }
        else if(averageGains != 0 && averageLosses == 0) {
            return new BigDecimal(100);
        }
        else {
            double RS = averageGains / averageLosses;
            double RSIvalue = 100 - (100 / (1 + RS));
            return new BigDecimal(RSIvalue).setScale(2, RoundingMode.DOWN);
        }
    }

    @Override
    /* Helper method for RSI. */
    public double calculateAverageValueForRSI(List<BigDecimal> list) {
        return list.stream().skip(1).mapToDouble(BigDecimal::doubleValue).sum() / (list.size());
    }

    @Override
    /* Helper method for RSI. */
    public void calculateGainsAndLossesForRSI(List<BigDecimal> gains, List<BigDecimal> losses) {
        for(int i=0; i<this.stocks.size(); i++) {
            if(i != 0) {
                BigDecimal prevPrice = this.stocks.get(i - 1).getLastTransactionPrice();
                BigDecimal currPrice = this.stocks.get(i).getLastTransactionPrice();

                if(currPrice.compareTo(prevPrice) < 0) {
                    losses.add(prevPrice.subtract(currPrice));
                    gains.add(BigDecimal.ZERO);
                }
                else if(currPrice.compareTo(prevPrice) > 0) {
                    gains.add(currPrice.subtract(prevPrice));
                    losses.add(BigDecimal.ZERO);
                }
                else {
                    gains.add(BigDecimal.ZERO);
                    losses.add(BigDecimal.ZERO);
                }
            }
        }
    }

    @Override
    /* Helper method for CCI. */
    public double calculateMeanDeviationForCCI(List<Double> typicalPrices, double SMA) {
        int n = typicalPrices.size();
        double sum = 0;
        for(Double price : typicalPrices) {
            sum += Math.abs(price - SMA);
        }

        return sum / n;
    }

    @Override
    /* Helper method for the indicators that require
     * typical price calculation inside their formula. */
    public List<Double> calculateTypicalPricesForGivenStocks(List<StockDetailsHistory> stocks) {
        List<Double> typicalPrices = new ArrayList<>();

        for(StockDetailsHistory stock : stocks) {
            double high = stock.getMaxPrice().doubleValue();
            double close = stock.getLastTransactionPrice().doubleValue();
            double low = stock.getMinPrice().doubleValue();
            double res = (high + low + close) / 3;

            typicalPrices.add(res);
        }

        return typicalPrices;
    }

    @Override
    /* Helper method for indicators that require
     * calculation of SMA inside their formula. */
    public double calculateSMAForGivenPrices(List<Double> prices) {
        return prices.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    }

    @Override
    public double calculateWMA(List<Double> stockPrices, int period) {
        double sum = 0;
        int n = stockPrices.size();
        for(int i=0; i<n; i++) {
            sum += stockPrices.get(n - 1 - i) * (n - i);
        }

        return sum / ((n * (n + 1)) / 2.0);
    }

    @Override
    /* This method sums up the signal based on the indicators and on the timeframe (short, long). */
    public String getSignalFromIndicatorsByTimeframe(double currentPrice, double smaValue, double emaValue, double hmaValue, double vwmaValue, double ichimokuBaselineValue) {
        if(smaValue == 0) {
            return "Hold";
        }

        List<Boolean> signalsList = new ArrayList<>();
        signalsList.add(currentPrice > smaValue);
        signalsList.add(currentPrice > emaValue);
        signalsList.add(currentPrice > hmaValue);
        signalsList.add(currentPrice > vwmaValue);
        signalsList.add(currentPrice > ichimokuBaselineValue);

        int signalSum = signalsList.stream().map(v -> v ? 1 : 0).mapToInt(Integer::intValue).sum();

        boolean isPriceTrendingUp = hmaValue > emaValue && emaValue > smaValue;
        boolean isPriceTrendingDown = hmaValue < emaValue && emaValue < smaValue;

        if(signalSum >= 4 || signalSum == 3 && isPriceTrendingUp) {
            return "Buy";
        }
        else if(signalSum < 2 || signalSum == 2 && isPriceTrendingDown) {
            return "Sell";
        }
        else {
            return "Hold";
        }
    }

    @Override
    /* This method sums up the final signal by combining and comparing the
     * 5 oscillator signals and the one signal for the 5 indicators together. */
    public String getFinalSignalByTimeframe(int timeframe, int numberOfStocksAvailable, List<TechnicalIndicator> indicatorsList, List<TechnicalIndicator> oscillatorsList) {
        if(numberOfStocksAvailable < timeframe) {
            return "Not Available";
        }

        List<String> oscillatorSignals = new ArrayList<>();
        for(TechnicalIndicator oscillator : oscillatorsList) {
            if(timeframe == 7) {
                oscillatorSignals.add(oscillator.getShortTermSignal());
            }
            else {
                oscillatorSignals.add(oscillator.getLongTermSignal());
            }
        }

        String indicatorsSignal;
        if(timeframe == 7) {
            indicatorsSignal = getSignalFromIndicatorsByTimeframe(
                    this.stocks.getLast().getLastTransactionPrice().doubleValue(),
                    indicatorsList.get(0).getValueByWeek().doubleValue(),
                    indicatorsList.get(1).getValueByWeek().doubleValue(),
                    indicatorsList.get(2).getValueByWeek().doubleValue(),
                    indicatorsList.get(3).getValueByWeek().doubleValue(),
                    indicatorsList.get(4).getValueByWeek().doubleValue());
        }
        else {
            indicatorsSignal = getSignalFromIndicatorsByTimeframe(
                    this.stocks.getLast().getLastTransactionPrice().doubleValue(),
                    indicatorsList.get(0).getValueByMonth().doubleValue(),
                    indicatorsList.get(1).getValueByMonth().doubleValue(),
                    indicatorsList.get(2).getValueByMonth().doubleValue(),
                    indicatorsList.get(3).getValueByMonth().doubleValue(),
                    indicatorsList.get(4).getValueByMonth().doubleValue());
        }

        Map<String, Integer> signalsMap = new HashMap<>();
        signalsMap.put("Hold", 0);
        signalsMap.put("Buy", 0);
        signalsMap.put("Sell", 0);

        signalsMap.computeIfPresent(indicatorsSignal, (key, value) -> value + 1);

        for(String signal : oscillatorSignals) {
            signalsMap.computeIfPresent(signal, (key, value) -> value + 1);
        }

        String finalSignal = "Hold";
        int max = signalsMap.get(finalSignal);
        int buyCount = signalsMap.get("Buy");
        int sellCount = signalsMap.get("Sell");
        boolean duplicates = false;

        if(buyCount == max) {
            duplicates = true;
        }
        else if(buyCount > max) {
            max = buyCount;
            finalSignal = "Buy";
        }

        if(sellCount == max && duplicates) {
            return finalSignal;
        }
        else if(sellCount > max){
            finalSignal = "Sell";
        }

        return finalSignal;
    }

    @Override
    /* This method returns a list of two signals,
     * one for short term trading and one for long term. */
    public List<String> getFinalSignalsList(Long companyId) {
        List<String> finalSignals = new ArrayList<>();
        this.stocks = this.stockDetailsService.findLast30ByCompanyId(companyId);
        finalSignals.add(getFinalSignalByTimeframe(7, getLastNStocks(7).size(), getTechnicalIndicators(companyId), getTechnicalOscillators(companyId)));
        finalSignals.add(getFinalSignalByTimeframe(30, getLastNStocks(30).size(), getTechnicalIndicators(companyId), getTechnicalOscillators(companyId)));
        return finalSignals;
    }

}