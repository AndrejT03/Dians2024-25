package com.dians.stocks.repository;

import com.dians.stocks.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
 Optional<Company> findCompanyByCode(String code);
 Page<Company> findAll(Pageable pageable);
}
