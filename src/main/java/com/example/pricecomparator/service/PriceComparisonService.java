package com.example.pricecomparator.service;

import com.example.pricecomparator.model.BestPriceResult;
import com.example.pricecomparator.model.DiscountProduct;
import com.example.pricecomparator.model.Product;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PriceComparisonService {
    public List<BestPriceResult> getBestPrices(List<Product> products, List<DiscountProduct> discountProducts) {
        Map<String, BestPriceResult> bestPrices = new HashMap<>();

        for (Product product : products) {
            double price = product.getPrice();
            boolean discounted = false;

            for (DiscountProduct discountProduct : discountProducts) {
                if (discountProduct.getProductId().equals(product.getProductId()) && discountProduct.getStore().equals(product.getStore()) && isDiscountValid(discountProduct, product.getDate())) {
                    price = price - (price * discountProduct.getPercentageOfDiscount() / 100.0);
                    discounted = true;
                    break;
                }
            }
            BestPriceResult currentBestPrice = bestPrices.get(product.getProductId());
            if (currentBestPrice == null || price < currentBestPrice.getFinalPrice()) {
                BestPriceResult newBest = new BestPriceResult();
                newBest.setProductId(product.getProductId());
                newBest.setProductName(product.getProductName());
                newBest.setStore(product.getStore());
                newBest.setPrice(product.getPrice());
                newBest.setFinalPrice(Math.round(price * 100.0) / 100.0);
                newBest.setDiscounted(discounted);
                bestPrices.put(product.getProductId(), newBest);
            }
        }
        return new ArrayList<>(bestPrices.values());
    }

    private boolean isDiscountValid(DiscountProduct discountProduct, LocalDate productDate) {
        return (productDate.isEqual(discountProduct.getFromDate()) || productDate.isAfter(discountProduct.getFromDate())) &&
                (productDate.isEqual(discountProduct.getToDate()) || productDate.isBefore(discountProduct.getToDate()));
    }
}
