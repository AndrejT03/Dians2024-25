package com.dians.stocks.service;

import com.dians.stocks.domain.Company;
import com.dians.stocks.dto.CompanyDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CompanyService {
  List<CompanyDTO> findAllCompaniesDTO(String sort, int page, int pageSize);
  Optional<Company> save(Company company);
  Optional<Company> findCompanyByCode(String code);
  Optional<Company> findCompanyById(Long id);
  CompanyDTO findByIdToDTO(Long id);
  void deleteById(Long id);
  int countCompanies();
  CompanyDTO convertToCompanyDTO(Company company);

  Map<Long, String> getMapOfCompanyCodesAndIds();
}
