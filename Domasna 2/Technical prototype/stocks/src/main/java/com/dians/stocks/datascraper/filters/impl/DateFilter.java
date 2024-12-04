package com.dians.stocks.datascraper.filters.impl;

import com.dians.stocks.domain.Company;
import com.dians.stocks.service.CompanyService;
import com.dians.stocks.service.StockDetailsService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;

@Component
public class DateFilter extends WriteFilter{
  private final CompanyService companyService;

  public DateFilter(StockDetailsService stockDetailsService, CompanyService companyService) {
    super(stockDetailsService, companyService);
    this.companyService = companyService;
  }

  @Override
  public Map<String, String> execute(Map<String, String> codesAndDates) throws IOException {
    String todaysformattedDate = getTodaysFormattedDate();

    for(String code : codesAndDates.keySet()) {
      Long companyId = this.companyService.findCompanyByCode(code).orElseThrow(RuntimeException::new).getId();
      String latestDateForStock = this.companyService.findCompanyByCode(code).orElseThrow(RuntimeException::new).getLatestWrittenDate();
      boolean isStockHistoryEmpty = this.companyService.findCompanyByCode(code).orElseThrow(RuntimeException::new).isStockHistoryEmpty();

      if(!isStockHistoryEmpty) {
        if(!latestDateForStock.equals(todaysformattedDate)) {
          codesAndDates.put(code, latestDateForStock);
        }
        else {
          codesAndDates.put(code, todaysformattedDate);
        }
      }
      else {
        String[] parts = todaysformattedDate.split("\\.");
        int todaysYear = Integer.parseInt(parts[2]);

        for(int i=todaysYear-10; i<todaysYear; i++) {
          String queryString = getQueryString(false, 1, 1, 1, 1, i, i + 1, code);
          writeRows(companyId, code, queryString);
        }

        String queryString = getQueryString(true,1, 1, 1, 1, todaysYear, todaysYear+1, code);
        writeRows(companyId, code, queryString);

        String latestWrittenDate = this.companyService.findCompanyByCode(code).orElseThrow(RuntimeException::new).getLatestWrittenDate();
        codesAndDates.put(code, latestWrittenDate);
      }
    }

    return codesAndDates;
  }
}
