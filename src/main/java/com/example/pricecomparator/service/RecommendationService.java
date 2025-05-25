package com.example.pricecomparator.service;

import com.example.pricecomparator.model.DiscountProduct;
import com.example.pricecomparator.model.Product;
import com.example.pricecomparator.model.ProductRecommendation;
import com.example.pricecomparator.repository.CsvDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private CsvDataRepository csvDataRepository;

    public List<ProductRecommendation> findSubstitutesByProductName(String productName, LocalDate date) throws IOException {
        List<Product> allProducts = csvDataRepository.loadAllProducts(date);
        List<DiscountProduct> allDiscounts = csvDataRepository.loadAllDiscounts(date);

        Optional<Product> reference = allProducts.stream()
                .filter(p -> p.getProductName().equalsIgnoreCase(productName))
                .findFirst();

        if (reference.isEmpty()) return Collections.emptyList();

        String category = reference.get().getProductCategory();

        List<ProductRecommendation> result = new ArrayList<>();

        for (Product p : allProducts) {
            if (!p.getProductCategory().equalsIgnoreCase(category)) continue;

            double price = p.getPrice();
            boolean discounted = false;

            Optional<DiscountProduct> discount = csvDataRepository.findActiveDiscountForProduct(p, allDiscounts);
            if (discount.isPresent()) {
                price -= price * discount.get().getPercentageOfDiscount() / 100.0;
                discounted = true;
            }

            double valuePerUnit = price / p.getPackageQuantity();

            ProductRecommendation recommendation = new ProductRecommendation();
            recommendation.setProductId(p.getProductId());
            recommendation.setProductName(p.getProductName());
            recommendation.setBrand(p.getBrand());
            recommendation.setStore(p.getStore());
            recommendation.setPackageQuantity(p.getPackageQuantity());
            recommendation.setPackageUnit(p.getPackageUnit());
            recommendation.setFinalPrice(Math.round(price * 100.0) / 100.0);
            recommendation.setValuePerUnit(Math.round(valuePerUnit * 100.0) / 100.0);
            recommendation.setDiscounted(discounted);

            result.add(recommendation);
        }

        return result.stream()
                .sorted(Comparator.comparing(ProductRecommendation::getValuePerUnit))
                .collect(Collectors.toList());
    }


}
