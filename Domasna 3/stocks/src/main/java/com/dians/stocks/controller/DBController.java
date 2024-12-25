package com.dians.stocks.controller;

import com.dians.stocks.datascraper.pipe.Pipe;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DBController {
    private final Pipe pipe;

    public DBController(Pipe pipe) {
        this.pipe = pipe;
    }

    @GetMapping("/update-database")
    public ResponseEntity updateDatabase() {
        try {
            pipe.createFilters();
            pipe.executeFilters();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("200 OK");
    }
}
