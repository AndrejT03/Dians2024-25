package com.dians.stocks.controller;

import com.dians.stocks.dto.CompanyDTO;
import com.dians.stocks.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class PageController {

  @GetMapping("/home")
  public String getHomePage(Model model) {
    model.addAttribute("title", "Home");
    model.addAttribute("bodyContent", "home");
    return "master-template";
  }

  @GetMapping("/issuers")
  public String getIssuersPage(Model model) {
    model.addAttribute("title", "Issuers");
    model.addAttribute("bodyContent", "Issuers");
    return "master-template";
  }

  @GetMapping("/stocks/")
  public String getIssuerInformation(@RequestParam Long companyId, Model model) {
    model.addAttribute("title", "Stocks");
    model.addAttribute("bodyContent", "stocks");
    model.addAttribute("companyId", companyId);
    return "master-template";
  }
}
