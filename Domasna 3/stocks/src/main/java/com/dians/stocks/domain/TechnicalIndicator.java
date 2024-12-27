package com.dians.stocks.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalIndicator {
    public String name;
    public BigDecimal dayValue;
    public BigDecimal weekValue;
    public BigDecimal monthValue;
    public boolean hasEnoughDayData;
    public boolean hasEnoughWeekData;
    public boolean hasEnoughMonthData;

    public TechnicalIndicator(String name) {
        this.name = name;
    }
}