package com.dians.stocks.controller;

import com.dians.stocks.datascraper.pipe.Pipe;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DBController {
    private final Pipe pipe;

    public DBController(Pipe pipe) {
        this.pipe = pipe;
    }

    @PostMapping("/api/update-database")
    public void updateDatabase() {
        try {
            pipe.createFilters();
            pipe.executeFilters();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
