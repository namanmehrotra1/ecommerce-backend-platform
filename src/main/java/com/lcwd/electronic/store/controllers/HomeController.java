package com.lcwd.electronic.store.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/test")
    public String testing() {
        logger.info("Working Fine!");
        return "Welcome to Electronic Store";
    }
}
