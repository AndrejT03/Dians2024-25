package com.dians.stocks.controller;

import com.dians.stocks.dto.CompanyDTO;
import com.dians.stocks.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/table-data/companies/")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    ResponseEntity<Map<String, Object>> getCompanies(@RequestParam int page,
                                                     @RequestParam int pageSize,
                                                     @RequestParam String sort) {
        Map<String, Object> response = new HashMap<>();
        Page<CompanyDTO> companiesPage = this.companyService.findAllCompaniesDTO(sort, page, pageSize);
        response.put("companies", companiesPage.get().collect(Collectors.toList()));
        response.put("totalPageCount", companiesPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CompanyDTO>> getAllCompanyCodes() {
        return ResponseEntity.ok().body(this.companyService.getAllCompaniesDTO());
    }
}
