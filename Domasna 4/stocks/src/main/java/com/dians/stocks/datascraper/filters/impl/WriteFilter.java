package com.dians.stocks.datascraper.filters.impl;

import com.dians.stocks.datascraper.filters.IFilter;
import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.service.CompanyService;
import com.dians.stocks.service.StockDetailsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public abstract class WriteFilter implements IFilter<String, String> {
  static final String url = "https://www.mse.mk/mk/stats/symbolhistory/";
  private final StockDetailsService stockDetailsService;
  private final CompanyService companyService;

  public WriteFilter(StockDetailsService stockDetailsService, CompanyService companyService) {
    this.stockDetailsService = stockDetailsService;
    this.companyService = companyService;
  }

  @Override
  public abstract Map<String, String> execute(Map<String, String> map) throws IOException;

  public void writeRows(Long companyId, String code, String queryString) throws IOException {
    Document doc = Jsoup.connect(String.format("%s%s%s", url, code, queryString)).get();
    Elements rowElements = doc.select("#resultsTable tbody tr");

    rowElements.reversed().forEach(row -> {
      boolean flag = false;

      Elements infoElements = row.select("td");
      for(int i=0; i<infoElements.size(); i++) {
        if(infoElements.get(i).text() == null || infoElements.get(i).text().isEmpty()) {
          flag = true;
          break;
        }
      }

      LocalDate date = parseStringToLocalDate(infoElements.get(0).text());
      if(!flag && this.stockDetailsService.findByDateAndCompany(date, this.companyService.findCompanyById(companyId).orElseThrow(RuntimeException::new)).isEmpty()) {
        StockDetailsHistory stockDetails = new StockDetailsHistory();

        stockDetails.setDate(date);
        stockDetails.setLastTransactionPrice(new BigDecimal(this.stringToBigDecimalFormat(infoElements.get(1).text())));
        stockDetails.setMaxPrice(new BigDecimal(this.stringToBigDecimalFormat(infoElements.get(2).text())));
        stockDetails.setMinPrice(new BigDecimal(this.stringToBigDecimalFormat(infoElements.get(3).text())));
        stockDetails.setAveragePrice(new BigDecimal(this.stringToBigDecimalFormat(infoElements.get(4).text())));
        stockDetails.setAveragePercentage(new BigDecimal(this.stringToBigDecimalFormat(infoElements.get(5).text())));
        stockDetails.setQuantity(Integer.parseInt(this.stringToBigDecimalFormat(infoElements.get(6).text())));
        stockDetails.setTurnoverInBestDenars(new BigDecimal(this.stringToBigDecimalFormat(infoElements.get(7).text())));
        stockDetails.setTotalTurnoverInDenars(new BigDecimal(this.stringToBigDecimalFormat(infoElements.get(8).text())));

        this.stockDetailsService.addStockDetailToCompany(companyId, stockDetails);
      }
    });
  }

  public String stringToBigDecimalFormat(String number) {
    return number.replace(".", "").replace(",", ".");
  }

  public LocalDate parseStringToLocalDate(String dateString) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
    return LocalDate.parse(dateString, formatter);
  }

  public String getTodaysFormattedDate() {
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
    return today.format(formatter);
  }

  public String getQueryString(boolean lastCycle, int fromDay, int toDay, int fromMonth, int toMonth, int fromYear, int toYear, String code) {
    String startingFormattedDate = String.format("%d.%d.%d", fromDay, fromMonth, fromYear);
    if(lastCycle) {
      return String.format("?FromDate=%s&ToDate=%s&Code=%s", startingFormattedDate, getTodaysFormattedDate(), code);
    }
    else {
      String endingFormattedDate = String.format("%d.%d.%d", toDay, toMonth, toYear);
      return String.format("?FromDate=%s&ToDate=%s&Code=%s", startingFormattedDate, endingFormattedDate, code);
    }
  }

}