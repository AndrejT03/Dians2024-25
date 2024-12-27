package com.dians.stocks.service.impl;

import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.domain.TechnicalIndicator;
import com.dians.stocks.service.StockDetailsService;
import com.dians.stocks.service.TechnicalIndicatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class TechnicalIndicatorServiceImpl implements TechnicalIndicatorService {
    private final StockDetailsService stockDetailsService;
    private List<StockDetailsHistory> stocks;

    public TechnicalIndicatorServiceImpl(StockDetailsService stockDetailsService) {
        this.stockDetailsService = stockDetailsService;
        this.stocks = new ArrayList<>();
    }

    @Override
    public List<TechnicalIndicator> getTechnicalIndicators(Long companyId) {
        this.stocks = this.stockDetailsService.findLast30ByCompanyId(companyId).reversed();
        List<TechnicalIndicator> indicatorsList = new ArrayList<>();
        indicatorsList.add(calculateRSI());
        indicatorsList.add(calculateROC());
        indicatorsList.add(calculateWPR());
        indicatorsList.add(calculateSMA());
        indicatorsList.add(calculateEMA());

        return indicatorsList;
    }

    @Override
    public TechnicalIndicator calculateRSI() {
        TechnicalIndicator RSI = new TechnicalIndicator("Relative Strength Index (RSI)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(RSI);

        List<BigDecimal> gains = new ArrayList<>();
        List<BigDecimal> losses = new ArrayList<>();

        calculateGainsAndLossesForRSI(gains, losses);

        RSI.setDayValue(calculateRSIValuesByTimeframe(1, RSI.isHasEnoughDayData(), gains, losses));
        RSI.setWeekValue(calculateRSIValuesByTimeframe(6, RSI.isHasEnoughWeekData(), gains, losses));
        RSI.setMonthValue(calculateRSIValuesByTimeframe(29, RSI.isHasEnoughMonthData(), gains, losses));

        return RSI;
    }

    @Override
    public BigDecimal calculateRSIValuesByTimeframe(int numberOfDays, boolean hasEnoughData, List<BigDecimal> gains, List<BigDecimal> losses) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

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
    public double calculateAverageValueForRSI(List<BigDecimal> list) {
        return list.stream().mapToDouble(BigDecimal::doubleValue).sum() / (list.size() + 1);
    }

    @Override
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
    public List<StockDetailsHistory> getLastNStocks(int count) {
        if (this.stocks == null || this.stocks.isEmpty() || count <= 1) {
            return new ArrayList<>();
        }

        int size = this.stocks.size();
        return this.stocks.subList(Math.max(size - count, 0), size);
    }

    @Override
    public List<BigDecimal> getLastNValues(List<BigDecimal> list, int count) {
        int size = list.size();
        return list.subList(Math.max(size - count, 0), size);
    }

    @Override
    public void checkIfIssuerHasEnoughStocksForTechnicalIndicator(TechnicalIndicator indicator) {
        if(this.stocks.size() < 2) {
            indicator.setHasEnoughDayData(false);
            indicator.setHasEnoughWeekData(false);
            indicator.setHasEnoughMonthData(false);
        }
        else if(this.stocks.size() < 7) {
            indicator.setHasEnoughDayData(true);
            indicator.setHasEnoughWeekData(false);
            indicator.setHasEnoughMonthData(false);
        }
        else if(this.stocks.size() < 30) {
            indicator.setHasEnoughDayData(true);
            indicator.setHasEnoughWeekData(true);
            indicator.setHasEnoughMonthData(false);
        }
        else {
            indicator.setHasEnoughDayData(true);
            indicator.setHasEnoughWeekData(true);
            indicator.setHasEnoughMonthData(true);
        }
    }

    @Override
    public TechnicalIndicator calculateROC() {
        TechnicalIndicator ROC = new TechnicalIndicator("Rate of Change (ROC)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(ROC);

        ROC.setDayValue(calculateROCValuesByTimeframe(2, ROC.isHasEnoughDayData()));
        ROC.setWeekValue(calculateROCValuesByTimeframe(7, ROC.isHasEnoughWeekData()));
        ROC.setMonthValue(calculateROCValuesByTimeframe(30, ROC.isHasEnoughMonthData()));

        return ROC;
    }

    @Override
    public BigDecimal calculateROCValuesByTimeframe(int numberOfDays, boolean hasEnoughData) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

        int size = this.stocks.size();
        double currentPrice = this.stocks.getLast().getLastTransactionPrice().doubleValue();
        double previousPrice = this.stocks.get(size - numberOfDays).getLastTransactionPrice().doubleValue();
        double result = (currentPrice - previousPrice) / previousPrice * 100;
        return new BigDecimal(result).setScale(2, RoundingMode.DOWN);
    }

    @Override
    public TechnicalIndicator calculateWPR() {
        TechnicalIndicator WPR = new TechnicalIndicator("Williams Percent Range (%R)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(WPR);

        WPR.setDayValue(calculateWPRValuesByTimeframe(WPR.isHasEnoughDayData(), getLastNStocks(2)));
        WPR.setWeekValue(calculateWPRValuesByTimeframe(WPR.isHasEnoughWeekData(), getLastNStocks(7)));
        WPR.setMonthValue(calculateWPRValuesByTimeframe(WPR.isHasEnoughMonthData(), this.stocks));

        return WPR;
    }

    @Override
    public BigDecimal calculateWPRValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

        double close = stocks.getLast().getLastTransactionPrice().doubleValue();
        double highestHigh = 0;
        double lowestLow = 0;

        for(int i=0; i<stocks.size(); i++) {
            if(i == 0) {
                highestHigh = stocks.get(i).getMaxPrice().doubleValue();
                lowestLow = stocks.get(i).getMinPrice().doubleValue();
            }
            else {
                highestHigh = Double.max(stocks.get(i).getMaxPrice().doubleValue(), highestHigh);
                lowestLow = Double.min(stocks.get(i).getMaxPrice().doubleValue(), lowestLow);
            }
        }

        if(highestHigh == lowestLow) {
            return new BigDecimal(0);
        }

        double result = (highestHigh - close) / (highestHigh - lowestLow) * 100;
        return new BigDecimal(result).setScale(2, RoundingMode.DOWN);
    }

    @Override
    public TechnicalIndicator calculateSMA() {
        TechnicalIndicator SMA = new TechnicalIndicator("Simple Moving Average (SMA)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(SMA);

        SMA.setDayValue(calculateSMAValuesByTimeframe(SMA.isHasEnoughDayData(), getLastNStocks(2)));
        SMA.setWeekValue(calculateSMAValuesByTimeframe(SMA.isHasEnoughDayData(), getLastNStocks(7)));
        SMA.setMonthValue(calculateSMAValuesByTimeframe(SMA.isHasEnoughDayData(), this.stocks));

        return SMA;
    }

    @Override
    public BigDecimal calculateSMAValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

        return BigDecimal.valueOf(stocks.stream()
                .map(StockDetailsHistory::getLastTransactionPrice)
                .mapToDouble(BigDecimal::doubleValue).average().getAsDouble()).setScale(2, RoundingMode.DOWN);
    }

    @Override
    public TechnicalIndicator calculateEMA() {
        TechnicalIndicator EMA = new TechnicalIndicator("Exponential Moving Average (EMA)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(EMA);

        EMA.setDayValue(calculateEMAValuesByTimeframe(EMA.isHasEnoughDayData(), getLastNStocks(2)));
        EMA.setWeekValue(calculateEMAValuesByTimeframe(EMA.isHasEnoughWeekData(), getLastNStocks(7)));
        EMA.setMonthValue(calculateEMAValuesByTimeframe(EMA.isHasEnoughMonthData(), this.stocks));

        return EMA;
    }

    @Override
    public BigDecimal calculateEMAValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

        int size = stocks.size();
        double k = 2.0 / (size + 1);
        double EMA = stocks.getFirst().getLastTransactionPrice().doubleValue();
        for(StockDetailsHistory stock : stocks) {
            EMA = stock.getLastTransactionPrice().doubleValue() * k + (EMA * (1 - k));
        }

        return new BigDecimal(EMA).setScale(2, RoundingMode.DOWN);
    }


}
