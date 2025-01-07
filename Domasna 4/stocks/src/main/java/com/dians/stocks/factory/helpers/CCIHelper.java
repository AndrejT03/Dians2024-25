package com.dians.stocks.factory.helpers;

import com.dians.stocks.domain.StockDetailsHistory;

import java.util.ArrayList;
import java.util.List;

public class CCIHelper {

    // Calculates typical prices for a given list of stocks.
    public static List<Double> calculateTypicalPricesForGivenStocks(List<StockDetailsHistory> stocks) {
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

    // Calculates Mean Deviation for the given function parameters.
    public static double calculateMeanDeviationForCCI(List<Double> typicalPrices, double SMA) {
        int n = typicalPrices.size();
        double sum = 0;
        for(Double price : typicalPrices) {
            sum += Math.abs(price - SMA);
        }

        return sum / n;
    }

}
