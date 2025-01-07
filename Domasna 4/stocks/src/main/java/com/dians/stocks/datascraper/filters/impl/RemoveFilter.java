package com.dians.stocks.datascraper.filters.impl;

import com.dians.stocks.datascraper.filters.IFilter;
import com.dians.stocks.domain.Company;
import com.dians.stocks.service.CompanyService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RemoveFilter implements IFilter<String, String> {
  private final CompanyService companyService;
  public RemoveFilter(CompanyService companyService) {
    this.companyService = companyService;
  }

  @Override
  /* This method is checking all the existing companies in the database
  * and their stock information. If there are unimportant days it removes them
  * and that way we get smaller but more efficient database. */
  public Map<String, String> execute(Map<String, String> codeDateMap) throws IOException {
    Map<String, String> updatedCodeDateMap = new HashMap<>();
    codeDateMap.keySet().forEach(key -> {
      Company company = this.companyService.findCompanyByCode(key).orElseThrow(() -> new RuntimeException("Company not found!"));
      if(company.isStockHistoryEmpty()) {
        this.companyService.deleteById(company.getId());}
      else {
        updatedCodeDateMap.put(key, company.getLatestWrittenDate());}});

    return updatedCodeDateMap;
  }
}
