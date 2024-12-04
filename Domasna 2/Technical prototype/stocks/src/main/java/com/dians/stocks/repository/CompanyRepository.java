package com.dians.stocks.repository;

import com.dians.stocks.domain.Company;
import com.dians.stocks.domain.StockDetailsHistory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
 Optional<Company> findCompanyByCode(String code);
}
