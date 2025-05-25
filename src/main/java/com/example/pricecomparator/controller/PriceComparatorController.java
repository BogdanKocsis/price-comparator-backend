package com.example.pricecomparator.controller;

import com.example.pricecomparator.model.*;
import com.example.pricecomparator.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prices")
public class PriceComparatorController {

    @Autowired
    private BasketOptimizerService basketOptimizerService;
    @Autowired
    private BestDiscountService bestDiscountService;
    @Autowired
    private NewDiscountService newDiscountService;
    @Autowired
    private PriceHistoryService priceHistoryService;
    @Autowired
    private RecommendationService recommendationService;

    @PostMapping("/optimize-basket")
    public Map<String, List<BestPriceResult>> optimizeBasket(@RequestBody List<String> basket, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws IOException {
        return basketOptimizerService.optimizeShoppingList(basket, date);
    }

    @GetMapping("/best-discounts")
    public List<DiscountProduct> getBestDiscounts(@RequestParam(defaultValue = "5") int limit, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws IOException {
        return bestDiscountService.getBestDiscounts(limit, date);
    }

    @GetMapping("/new-discounts")
    public List<DiscountProduct> getNewDiscounts(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws IOException {
        return newDiscountService.getNewlyAddedDiscounts(date);
    }

    @GetMapping("/price-history")
    public List<PriceHistoryPoint> getPriceHistory(
            @RequestParam String productName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String store
    ) throws IOException {
        return priceHistoryService.getPriceHistoryByProductName(productName, category, store);
    }

    @GetMapping("/recommendations")
    public List<ProductRecommendation> getRecommendations(
            @RequestParam String productName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) throws IOException {
        return recommendationService.findSubstitutesByProductName(productName, date);
    }

}
