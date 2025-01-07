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
    /* Starts the creation and execution of the filters inside

the pipe in order to create or update the tables in the database. */
    public void updateDatabase() {
        try {
            pipe.createFilters();
            pipe.executeFilters();} catch (Exception e) {
            e.printStackTrace();}}
}
