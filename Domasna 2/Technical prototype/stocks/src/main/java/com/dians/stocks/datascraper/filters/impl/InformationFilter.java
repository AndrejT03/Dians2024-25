package com.dians.stocks.datascraper.filters.impl;

import com.dians.stocks.domain.Company;
import com.dians.stocks.service.CompanyService;
import com.dians.stocks.service.StockDetailsService;

import java.io.*;
import java.util.Map;

public class InformationFilter extends WriteFilter {
  private final CompanyService companyService;

  public InformationFilter(StockDetailsService stockDetailsService, CompanyService companyService) {
    super(stockDetailsService, companyService);
    this.companyService = companyService;
  }

  @Override
  public Map<String, String> execute(Map<String, String> codeDateMap) throws IOException {
    String todaysFormattedDate = getTodaysFormattedDate();

    for (Map.Entry<String, String> entry : codeDateMap.entrySet()) {
      String code = entry.getKey();
      String date = entry.getValue();

      if (!date.equals(todaysFormattedDate)) {
        String[] writtenDateParts = date.split("\\.");
        String[] todaysDateParts = todaysFormattedDate.split("\\.");
        int writtenDay = Integer.parseInt(writtenDateParts[0]);
        int writtenMonth = Integer.parseInt(writtenDateParts[1]);
        int writtenYear = Integer.parseInt(writtenDateParts[2]);
        int todaysDay = Integer.parseInt(todaysDateParts[0]);
        int todaysMonth = Integer.parseInt(todaysDateParts[1]);
        int todaysYear = Integer.parseInt(todaysDateParts[2]);

        Long companyId = this.companyService.findCompanyByCode(code).orElseThrow(RuntimeException::new).getId();

        if (todaysYear - writtenYear > 1) {
          String queryString = getQueryString(false, writtenDay, 1, writtenMonth, 1, writtenYear, writtenYear + 1, code);
          writeRows(companyId, code, queryString);

          for (int i = writtenYear + 1; i < todaysYear; i++) {
            queryString = getQueryString(false, 1, 1, 1, 1, i, i + 1, code);
            writeRows(companyId, code, queryString);
          }

          queryString = getQueryString(true, 1, todaysDay, 1, todaysMonth, todaysYear, todaysYear, code);
          writeRows(companyId, code, queryString);

        }
        else if (todaysYear - writtenYear == 1) {
          String queryString = getQueryString(false, writtenDay, 1, writtenMonth, 1, writtenYear, todaysYear, code);
          writeRows(companyId, code, queryString);
          queryString = getQueryString(true, 1, todaysDay, 1, todaysMonth, todaysYear, todaysYear, code);
          writeRows(companyId, code, queryString);

        }
        else {
          String queryString = getQueryString(true, writtenDay, todaysDay, writtenMonth, todaysMonth, writtenYear, todaysYear, code);
          writeRows(companyId, code, queryString);
        }

        String lastWrittenDate = this.companyService.findCompanyByCode(code).orElseThrow(RuntimeException::new).getLatestWrittenDate();
        codeDateMap.put(code, lastWrittenDate);
      }
    }

    return codeDateMap;
  }
}
