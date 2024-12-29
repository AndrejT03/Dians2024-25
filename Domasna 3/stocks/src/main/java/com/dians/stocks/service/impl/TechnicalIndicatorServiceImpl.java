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
    public List<TechnicalIndicator> getTechnicalIndicators(Long companyId) {
        this.stocks = this.stockDetailsService.findLast30ByCompanyId(companyId).reversed();
        List<TechnicalIndicator> indicatorsList = new ArrayList<>();

        indicatorsList.add(calculateSMA());
        indicatorsList.add(calculateEMA());
        indicatorsList.add(calculateHMA());
        indicatorsList.add(calculateVWMA());
        indicatorsList.add(calculateIBL());

        return indicatorsList;
    }

    @Override
    public List<TechnicalIndicator> getTechnicalOscillators(Long companyId) {
        this.stocks = this.stockDetailsService.findLast30ByCompanyId(companyId).reversed();
        List<TechnicalIndicator> oscillatorsList = new ArrayList<>();

        oscillatorsList.add(calculateRSI());
        oscillatorsList.add(calculateROC());
        oscillatorsList.add(calculateWPR());
        oscillatorsList.add(calculateSTO());
        oscillatorsList.add(calculateCCI());

        return oscillatorsList;
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
    public TechnicalIndicator calculateRSI() {
        TechnicalIndicator RSI = new TechnicalIndicator("Relative Strength Index (RSI)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(RSI);

        List<BigDecimal> gains = new ArrayList<>();
        List<BigDecimal> losses = new ArrayList<>();

        calculateGainsAndLossesForRSI(gains, losses);

        RSI.setDayValue(calculateRSIValuesByTimeframe(1, RSI.isHasEnoughDayData(), gains, losses));
        RSI.setWeekValue(calculateRSIValuesByTimeframe(6, RSI.isHasEnoughWeekData(), gains, losses));
        RSI.setMonthValue(calculateRSIValuesByTimeframe(29, RSI.isHasEnoughMonthData(), gains, losses));

        RSI.setShortTermSignal(getRSISignal(RSI.getWeekValue().doubleValue()));
        RSI.setLongTermSignal(getRSISignal(RSI.getMonthValue().doubleValue()));

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
    public String getRSISignal(double value) {
        if(value < 30) {
            return "Buy";
        }
        else if(value > 70) {
            return "Sell";
        }
        else {
            return "Hold";
        }
    }

    @Override
    public TechnicalIndicator calculateROC() {
        TechnicalIndicator ROC = new TechnicalIndicator("Rate of Change (ROC)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(ROC);

        ROC.setDayValue(calculateROCValuesByTimeframe(2, ROC.isHasEnoughDayData()));
        ROC.setWeekValue(calculateROCValuesByTimeframe(7, ROC.isHasEnoughWeekData()));
        ROC.setMonthValue(calculateROCValuesByTimeframe(30, ROC.isHasEnoughMonthData()));

        ROC.setShortTermSignal(getROCSignal(ROC.getWeekValue().doubleValue()));
        ROC.setLongTermSignal(getROCSignal(ROC.getMonthValue().doubleValue()));

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
    public String getROCSignal(double value) {
        if(value < -5) {
            return "Sell";
        }
        else if(value > 5) {
            return "Buy";
        }
        else {
            return "Hold";
        }
    }

    @Override
    public TechnicalIndicator calculateWPR() {
        TechnicalIndicator WPR = new TechnicalIndicator("Williams Percent Range (%R)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(WPR);

        WPR.setDayValue(calculateWPRValuesByTimeframe(WPR.isHasEnoughDayData(), getLastNStocks(2)));
        WPR.setWeekValue(calculateWPRValuesByTimeframe(WPR.isHasEnoughWeekData(), getLastNStocks(7)));
        WPR.setMonthValue(calculateWPRValuesByTimeframe(WPR.isHasEnoughMonthData(), this.stocks));

        WPR.setShortTermSignal(getWPRSignal(WPR.getWeekValue().doubleValue()));
        WPR.setLongTermSignal(getWPRSignal(WPR.getMonthValue().doubleValue()));

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

        double result = (highestHigh - close) / (highestHigh - lowestLow) * -100;
        return new BigDecimal(result).setScale(2, RoundingMode.DOWN);
    }

    @Override
    public String getWPRSignal(double value) {
        if(value < -80) {
            return "Buy";
        }
        else if(value > -20) {
            return "Sell";
        }
        else {
            return "Hold";
        }
    }

    @Override
    public TechnicalIndicator calculateSTO() {
        TechnicalIndicator STO = new TechnicalIndicator("Stochastic Oscillator (%K)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(STO);

        STO.setDayValue(calculateSTOValuesByTimeframe(STO.isHasEnoughDayData(), getLastNStocks(2)));
        STO.setWeekValue(calculateSTOValuesByTimeframe(STO.isHasEnoughWeekData(), getLastNStocks(7)));
        STO.setMonthValue(calculateSTOValuesByTimeframe(STO.isHasEnoughMonthData(), this.stocks));

        STO.setShortTermSignal(getSTOSignal(STO.getWeekValue().doubleValue()));
        STO.setLongTermSignal(getSTOSignal(STO.getMonthValue().doubleValue()));

        return STO;
    }

    @Override
    public BigDecimal calculateSTOValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

        double currentClose = stocks.getLast().getLastTransactionPrice().doubleValue();
        double highestHigh = stocks.getFirst().getMaxPrice().doubleValue();
        double lowestLow = stocks.getFirst().getMinPrice().doubleValue();

        for(int i=1; i<stocks.size(); i++) {
            highestHigh = Math.max(highestHigh, stocks.get(i).getMaxPrice().doubleValue());
            lowestLow = Math.min(lowestLow, stocks.get(i).getMinPrice().doubleValue());
        }

        if(highestHigh == lowestLow) {
            return new BigDecimal(0);
        }

        double result = (currentClose - lowestLow) / (highestHigh - lowestLow) * 100;
        return new BigDecimal(result).setScale(2, RoundingMode.DOWN);
    }

    @Override
    public String getSTOSignal(double value) {
        if(value < 20) {
            return "Buy";
        }
        else if(value > 80) {
            return "Sell";
        }
        else {
            return "Hold";
        }
    }

    @Override
    public TechnicalIndicator calculateCCI() {
        TechnicalIndicator CCI = new TechnicalIndicator("Commodity Channel Index (CCI)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(CCI);

        CCI.setDayValue(calculateCCIValuesByTimeframe(CCI.isHasEnoughDayData(), getLastNStocks(2)));
        CCI.setWeekValue(calculateCCIValuesByTimeframe(CCI.isHasEnoughDayData(), getLastNStocks(7)));
        CCI.setMonthValue(calculateCCIValuesByTimeframe(CCI.isHasEnoughDayData(), this.stocks));

        CCI.setShortTermSignal(getCCISignal(CCI.getWeekValue().doubleValue()));
        CCI.setLongTermSignal(getCCISignal(CCI.getMonthValue().doubleValue()));

        return CCI;
    }

    @Override
    public BigDecimal calculateCCIValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

        List<Double> typicalPrices = calculateTypicalPricesForGivenStocks(stocks);
        double SMA = calculateSMAForGivenPrices(typicalPrices);
        double meanDeviation = calculateMeanDeviationForCCI(typicalPrices, SMA);

        if(meanDeviation == 0) {
            return new BigDecimal(0);
        }

        double lastTP = typicalPrices.getLast();

        double result = (lastTP - SMA) / (0.015 * meanDeviation);
        return new BigDecimal(result).setScale(2, RoundingMode.DOWN);
    }

    @Override
    public double calculateMeanDeviationForCCI(List<Double> typicalPrices, double SMA) {
        int n = typicalPrices.size();
        double sum = 0;
        for(Double price : typicalPrices) {
            sum += Math.abs(price - SMA);
        }

        return sum / n;
    }

    @Override
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
    public String getCCISignal(double value) {
        if(value > 100) {
            return "Buy";
        }
        else if(value < -100) {
            return "Sell";
        }
        else {
            return "Hold";
        }
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

        List<Double> prices = stocks.stream()
                .map(StockDetailsHistory::getLastTransactionPrice)
                .mapToDouble(BigDecimal::doubleValue)
                .boxed()
                .toList();
        double result = calculateSMAForGivenPrices(prices);
        return new BigDecimal(result).setScale(2, RoundingMode.DOWN);
    }

    @Override
    public double calculateSMAForGivenPrices(List<Double> prices) {
        return prices.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
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

    @Override
    public TechnicalIndicator calculateVWMA() {
        TechnicalIndicator VWMA = new TechnicalIndicator("Volume Weighted Moving Average (VWMA)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(VWMA);

        VWMA.setDayValue(calculateVWMAValuesByTimeframe(VWMA.isHasEnoughDayData(), getLastNStocks(2)));
        VWMA.setWeekValue(calculateVWMAValuesByTimeframe(VWMA.isHasEnoughWeekData(), getLastNStocks(7)));
        VWMA.setMonthValue(calculateVWMAValuesByTimeframe(VWMA.isHasEnoughMonthData(), this.stocks));

        return VWMA;
    }

    @Override
    public BigDecimal calculateVWMAValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

        double totalWeightedPrice = 0;
        double totalVolume = 0;

        for(StockDetailsHistory stock : stocks) {
            totalWeightedPrice += (stock.getLastTransactionPrice().doubleValue() * stock.getQuantity());
            totalVolume += stock.getQuantity();
        }

        double result = totalVolume != 0 ? totalWeightedPrice / totalVolume : 0;
        return new BigDecimal(result).setScale(2, RoundingMode.DOWN);
    }

    @Override
    public TechnicalIndicator calculateHMA() {
        TechnicalIndicator HMA = new TechnicalIndicator("Hull Moving Average (HMA)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(HMA);

        HMA.setDayValue(calculateHMAValuesByTimeframe(HMA.isHasEnoughDayData(), getLastNStocks(2)));
        HMA.setWeekValue(calculateHMAValuesByTimeframe(HMA.isHasEnoughWeekData(), getLastNStocks(7)));
        HMA.setMonthValue(calculateHMAValuesByTimeframe(HMA.isHasEnoughMonthData(), this.stocks));

        return HMA;
    }

    @Override
    public BigDecimal calculateHMAValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

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

        return BigDecimal.valueOf(hmaList.getLast()).setScale(2, RoundingMode.DOWN);
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
    public TechnicalIndicator calculateIBL() {
        TechnicalIndicator IBL = new TechnicalIndicator("Ichimoku Baseline (IBL)");

        checkIfIssuerHasEnoughStocksForTechnicalIndicator(IBL);

        IBL.setDayValue(calculateIBLValuesByTimeframe(IBL.isHasEnoughDayData(), getLastNStocks(2)));
        IBL.setWeekValue(calculateIBLValuesByTimeframe(IBL.isHasEnoughWeekData(), getLastNStocks(7)));
        IBL.setMonthValue(calculateIBLValuesByTimeframe(IBL.isHasEnoughMonthData(), this.stocks));

        return IBL;
    }

    @Override
    public BigDecimal calculateIBLValuesByTimeframe(boolean hasEnoughData, List<StockDetailsHistory> stocks) {
        if(!hasEnoughData) {
            return new BigDecimal(-999);
        }

        double highestHigh = stocks.getFirst().getMaxPrice().doubleValue();
        double lowestLow = stocks.getFirst().getMinPrice().doubleValue();

        for(int i=1; i<stocks.size(); i++) {
            highestHigh = Math.max(highestHigh, stocks.get(i).getMaxPrice().doubleValue());
            lowestLow = Math.min(lowestLow, stocks.get(i).getMinPrice().doubleValue());
        }

        double result = (highestHigh + lowestLow) / 2;
        return new BigDecimal(result).setScale(2, RoundingMode.DOWN);
    }

    @Override
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
                    indicatorsList.get(0).getWeekValue().doubleValue(),
                    indicatorsList.get(1).getWeekValue().doubleValue(),
                    indicatorsList.get(2).getWeekValue().doubleValue(),
                    indicatorsList.get(3).getWeekValue().doubleValue(),
                    indicatorsList.get(4).getWeekValue().doubleValue());
        }
        else {
            indicatorsSignal = getSignalFromIndicatorsByTimeframe(
                    this.stocks.getLast().getLastTransactionPrice().doubleValue(),
                    indicatorsList.get(0).getMonthValue().doubleValue(),
                    indicatorsList.get(1).getMonthValue().doubleValue(),
                    indicatorsList.get(2).getMonthValue().doubleValue(),
                    indicatorsList.get(3).getMonthValue().doubleValue(),
                    indicatorsList.get(4).getMonthValue().doubleValue());
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
    public List<String> getFinalSignalsList(Long companyId) {
        List<String> finalSignals = new ArrayList<>();
        this.stocks = this.stockDetailsService.findLast30ByCompanyId(companyId);
        finalSignals.add(getFinalSignalByTimeframe(7, getLastNStocks(7).size(), getTechnicalIndicators(companyId), getTechnicalOscillators(companyId)));
        finalSignals.add(getFinalSignalByTimeframe(30, getLastNStocks(30).size(), getTechnicalIndicators(companyId), getTechnicalOscillators(companyId)));
        return finalSignals;
    }

}