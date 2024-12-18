package com.dians.stocks.service.impl;

import com.dians.stocks.domain.Company;
import com.dians.stocks.dto.CompanyDTO;
import com.dians.stocks.repository.CompanyRepository;
import com.dians.stocks.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
  private final CompanyRepository companyRepository;

  @Override
  public Page<CompanyDTO> findAllCompaniesDTO(String sort, int page, int pageSize) {
    String sortBy = sort.split("-")[0];
    String order = sort.split("-")[1];

    Pageable pageable;
    if(order.equals("asc")) {
      pageable = PageRequest.of(page, pageSize, Sort.by(sortBy).ascending());
    }
    else {
      pageable = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
    }

    Page<Company> companiesPage = this.companyRepository.findAll(pageable);
    List<CompanyDTO> companiesList = companiesPage
        .stream()
        .map(this::convertToCompanyDTO).collect(Collectors.toList());

    return new PageImpl<>(companiesList, companiesPage.getPageable(), companiesPage.getTotalElements());
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

}
