package com.dians.stocks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

  @GetMapping
  @RequestMapping(value = {"", "/", "/home", "/companies"})
  public String getCompaniesPage(Model model) {
    model.addAttribute("title", "Companies");
    model.addAttribute("bodyContent", "Companies");
    return "master-template";
  }

  @GetMapping("/stocks")
  public String getStocksPage(Model model) {
    model.addAttribute("title", "Stocks");
    model.addAttribute("bodyContent", "stocks");
    return "master-template";
  }
}