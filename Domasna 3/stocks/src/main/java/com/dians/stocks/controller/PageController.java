package com.dians.stocks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

  @GetMapping
  @RequestMapping(value = {"", "/", "/home", "/issuers"})
  public String getIssuersPage(Model model) {
    model.addAttribute("title", "Issuers");
    model.addAttribute("bodyContent", "Issuers");
    return "master-template";
  }

  @GetMapping("/stocks")
  public String getIssuerInformation(Model model) {
    model.addAttribute("title", "Stocks");
    model.addAttribute("bodyContent", "stocks");
    return "master-template";
  }
}
