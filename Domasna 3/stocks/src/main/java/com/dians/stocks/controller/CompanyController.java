package com.dians.stocks.controller;

import com.dians.stocks.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/table-data/issuers/")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    ResponseEntity<Map<String, Object>> getCompanies(@RequestParam(required = false) String sort,
                                                     @RequestParam int page,
                                                     @RequestParam int pageSize) {
        Map<String, Object> response = new HashMap<>();
        response.put("companies", this.companyService.findAllCompaniesDTO(sort, page, pageSize));
        int totalNumberOfPages = (int) Math.ceil((double) this.companyService.countCompanies() / pageSize);
        response.put("totalCount", totalNumberOfPages);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Map<Long, String>> getAllCompanyCodes() {
        return ResponseEntity.ok().body(this.companyService.getMapOfCompanyCodesAndIds());
    }
}
