package com.dians.stocks.service.impl;

import com.dians.stocks.domain.Company;
import com.dians.stocks.dto.CompanyDTO;
import com.dians.stocks.repository.CompanyRepository;
import com.dians.stocks.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
  private final CompanyRepository companyRepository;

  public List<CompanyDTO> findAllCompaniesDTO(String sort, int page, int pageSize) {
    List<CompanyDTO> companies = this.companyRepository
        .findAll().stream()
        .map(this::convertToCompanyDTO)
        .collect(Collectors.toList());

    if(sort.equals("code-asc"))
      companies.sort(Comparator.comparing(CompanyDTO::getCode));
    else
      companies.sort(Comparator.comparing(CompanyDTO::getCode).reversed());

    return companies.stream().skip((long) page * pageSize).limit(pageSize).collect(Collectors.toList());
  }

  @Override
  public Optional<Company> save(Company company) {
    return Optional.of(this.companyRepository.save(company));
  }

  @Override
  public Optional<Company> findCompanyByCode(String code) {
    return this.companyRepository.findCompanyByCode(code);
  }

  @Override
  public Optional<Company> findCompanyById(Long id) {
    return this.companyRepository.findById(id);
  }

  @Override
  public CompanyDTO findByIdToDTO(Long id) {
    return this.companyRepository.findById(id)
        .map(this::convertToCompanyDTO)
        .orElseThrow(RuntimeException::new);
  }

  @Override
  public void deleteById(Long id) {
    this.companyRepository.deleteById(id);
  }

  @Override
  public int countCompanies() {
    return this.companyRepository.findAll().size();
  }

  // Vo mapper
  @Override
  public CompanyDTO convertToCompanyDTO(Company company) {
    return new CompanyDTO()
        .builder()
        .companyId(company.getId())
        .code(company.getCode())
        .name(company.getName())
        .latestTurnoverDate(company.getLatestWrittenDate())
        .build();
  }

  @Override
  public Map<Long, String> getMapOfCompanyCodesAndIds() {
    Map<Long, String> map = new TreeMap<>();
    this.companyRepository.findAll().forEach(c -> map.put(c.getId(), c.getCode()));
    return map;
  }

//  @Override
//  public List<String> getAllCompanyCodes() {
//    List<CompanyDTO> companies = new ArrayList<>();
//    this.companyRepository.findAll().forEach(c -> companies.add(this.convertToCompanyDTO(c)));
//    companies.sort(Comparator.comparing(CompanyDTO::getCode));
//    return companies.stream().map(CompanyDTO::getCode).collect(Collectors.toList());
//  }
}
