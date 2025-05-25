package com.example.pricecomparator.service;


import com.example.pricecomparator.model.BestPriceResult;
import com.example.pricecomparator.model.DiscountProduct;
import com.example.pricecomparator.model.Product;
import com.example.pricecomparator.repository.CsvDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BasketOptimizerService {

    @Autowired
    private CsvDataRepository csvDataRepository;
    @Autowired
    private PriceComparisonService priceComparisonService;

    public Map<String, List<BestPriceResult>> optimizeShoppingList(List<String> basket, LocalDate date) throws IOException {
        List<Product> products = csvDataRepository.loadAllProducts(date);
        List<DiscountProduct> discountProducts = csvDataRepository.loadAllDiscounts(date);

        List<BestPriceResult> bestPrices = priceComparisonService.getBestPrices(products, discountProducts);

        Map<String, List<BestPriceResult>> result = new HashMap<>();
        for (String item : basket) {
            bestPrices.stream()
                    .filter(p -> p.getProductName().equalsIgnoreCase(item))
                    .findFirst()
                    .ifPresent(p -> {
                        result.computeIfAbsent(p.getStore(), k -> new ArrayList<>()).add(p);
                    });
        }

        return result;
    }
}
