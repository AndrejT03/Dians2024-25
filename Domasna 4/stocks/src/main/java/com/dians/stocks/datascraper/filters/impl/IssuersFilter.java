package com.dians.stocks.datascraper.filters.impl;

import com.dians.stocks.datascraper.filters.IFilter;
import com.dians.stocks.domain.Company;
import com.dians.stocks.service.CompanyService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class IssuersFilter implements IFilter<String, String> {
  private static final String codesURL = "https://www.mse.mk/mk/stats/symbolhistory/kmb";
  private static final String namesURL = "https://www.mse.mk/en/symbol/";
  private final CompanyService companyService;

  public IssuersFilter(CompanyService companyService) {
    this.companyService = companyService;
  }

  @Override
  public Map<String, String> execute(Map<String, String> issuers) throws IOException {
    Document codesDoc = Jsoup.connect(codesURL).get();

    Elements elements = codesDoc.select(".dropdown option");
    for(Element el : elements) {
      String code = el.text();
      if(!code.matches(".*\\d.*")) {
        Document nameDoc = Jsoup.connect(namesURL + code).get();
        String name = nameDoc.select(".title").text();
        if(name == null || name.isEmpty()) {
          String[] parts = nameDoc.select("#titleKonf2011").text().split("-");
          name = parts[2].trim();
        }

        Optional<Company> companyOptional = this.companyService.findCompanyByCode(code);
        if(companyOptional.isEmpty()) {
          Company company = new Company(code, name);
          this.companyService.save(company);
          issuers.put(code, "");
        }
        else {
          issuers.put(code, companyOptional.get().getLatestWrittenDate());
        }
      }
    }

    return issuers;
  }
}
