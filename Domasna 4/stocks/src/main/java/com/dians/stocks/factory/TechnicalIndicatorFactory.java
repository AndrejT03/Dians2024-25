package com.dians.stocks.factory;

import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.domain.TechnicalIndicator;

import java.util.List;

public abstract class TechnicalIndicatorFactory {
  /* This is an abstract class setting the blueprint
  * for the two factories that are extending it. */

    protected List<StockDetailsHistory> stocks;

    public TechnicalIndicatorFactory(List<StockDetailsHistory> stocks) {
        this.stocks = stocks;
    }

    public abstract TechnicalIndicator createIndicator(String code, String name);

}
