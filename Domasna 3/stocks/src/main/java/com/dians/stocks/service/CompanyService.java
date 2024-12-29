package com.dians.stocks.service;

import com.dians.stocks.domain.Company;
import com.dians.stocks.dto.CompanyDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CompanyService {
  Page<CompanyDTO> findAllCompaniesDTO(String sort, int page, int pageSize);
  Optional<Company> save(Company company);
  Optional<Company> findCompanyByCode(String code);
  Optional<Company> findCompanyById(Long id);
  CompanyDTO findByIdToDTO(Long id);
  void deleteById(Long id);
  CompanyDTO convertToCompanyDTO(Company company);
  List<CompanyDTO> getAllCompaniesDTO();
}
