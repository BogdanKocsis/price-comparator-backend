package com.example.pricecomparator.service;

import com.example.pricecomparator.repository.CsvDataRepository;
import com.example.pricecomparator.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class PriceHistoryService {

    @Autowired
    private CsvDataRepository csvDataRepository;

    public List<PriceHistoryPoint> getPriceHistoryByProductName(String productName, String category, String store) throws IOException {
        List<DiscountProduct> discountProducts = csvDataRepository.loadAllOverDiscounts();
        List<PriceHistoryPoint> history = new ArrayList<>();
        List<Product> matchingProducts = csvDataRepository.filterProducts(productName, category, store);

        for (Product product : matchingProducts) {

            int discountPercent = csvDataRepository.findActiveDiscountForProduct(product, discountProducts)
                    .map(DiscountProduct::getPercentageOfDiscount)
                    .orElse(0);

            PriceHistoryPoint point = new PriceHistoryPoint();
            point.setProductId(product.getProductId());
            point.setProductName(product.getProductName());
            point.setStore(product.getStore());
            point.setDate(product.getDate());
            point.setPrice(product.getPrice());
            point.setDiscount(discountPercent != 0 ? discountPercent : 0);
            point.setDiscountedPrice(discountPercent != 0 ? Math.round((product.getPrice() - (product.getPrice() * discountPercent / 100)) * 100.0) / 100.0 : product.getPrice());
            history.add(point);
        }

        history.sort(Comparator.comparing(PriceHistoryPoint::getDate));
        return history;
    }

}