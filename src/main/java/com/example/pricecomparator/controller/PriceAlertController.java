package com.example.pricecomparator.controller;


import com.example.pricecomparator.service.PriceAlertService;
import com.example.pricecomparator.model.PriceAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class PriceAlertController {

    @Autowired
    private PriceAlertService priceAlertService;

    @PostMapping
    public void addAlert(@RequestBody PriceAlert alert) {
        priceAlertService.saveAlert(alert);
    }

    @GetMapping
    public List<PriceAlert> getAlerts() {
        return priceAlertService.getAlerts();
    }
}
