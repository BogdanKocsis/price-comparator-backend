// --- CsvDataRepository ---
package com.example.pricecomparator.repository;

import com.example.pricecomparator.model.Product;
import com.example.pricecomparator.model.DiscountProduct;
import com.example.pricecomparator.service.CsvParserService;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CsvDataRepository {

    private final CsvParserService csvParserService;
    private final List<String> stores = List.of("lidl", "profi", "kaufland");

    public CsvDataRepository(CsvParserService csvParserService) {
        this.csvParserService = csvParserService;
    }

    public List<Product> loadAllProducts(LocalDate date) throws IOException {
        List<Product> products = new ArrayList<>();
        List<String> productFiles = List.of(
                "src/data/lidl_" + date + ".csv",
                "src/data/profi_" + date + ".csv",
                "src/data/kaufland_" + date + ".csv"
        );

        for (int i = 0; i < productFiles.size(); i++) {
            products.addAll(csvParserService.parseProducts(productFiles.get(i), stores.get(i), date));
        }
        return products;
    }

    public List<Product> findByProductName(String productName) throws IOException {
        List<Product> allProducts = loadAllOverProducts();
        return allProducts.stream()
                .filter(p -> p.getProductName().equalsIgnoreCase(productName))
                .collect(Collectors.toList());
    }


    public List<DiscountProduct> loadAllDiscounts(LocalDate date) throws IOException {
        List<DiscountProduct> discountProducts = new ArrayList<>();
        List<String> discountFiles = List.of(
                "src/data/lidl_discounts_" + date + ".csv",
                "src/data/profi_discounts_" + date + ".csv",
                "src/data/kaufland_discounts_" + date + ".csv"
        );

        for (int i = 0; i < discountFiles.size(); i++) {
            discountProducts.addAll(csvParserService.parseDiscounts(discountFiles.get(i), stores.get(i)));
        }
        return discountProducts;
    }

    public List<Product> loadAllOverProducts() throws IOException {
        List<Product> allProducts = new ArrayList<>();
        Map<String, List<String>> storeToFiles = Map.of(
                "lidl", List.of("src/data/lidl_2025-05-01.csv", "src/data/lidl_2025-05-08.csv"),
                "profi", List.of("src/data/profi_2025-05-01.csv", "src/data/profi_2025-05-08.csv"),
                "kaufland", List.of("src/data/kaufland_2025-05-01.csv", "src/data/kaufland_2025-05-08.csv")
        );

        for (Map.Entry<String, List<String>> entry : storeToFiles.entrySet()) {
            String store = entry.getKey();
            for (String file : entry.getValue()) {
                LocalDate date = extractDateFromFilename(file);
                allProducts.addAll(csvParserService.parseProducts(file, store, date));
            }
        }
        return allProducts;
    }

    public List<DiscountProduct> loadAllOverDiscounts() throws IOException {
        List<DiscountProduct> allDiscountProducts = new ArrayList<>();
        List<String> discountFiles = List.of(
                "src/data/lidl_discounts_2025-05-01.csv",
                "src/data/lidl_discounts_2025-05-08.csv",
                "src/data/profi_discounts_2025-05-01.csv",
                "src/data/profi_discounts_2025-05-08.csv",
                "src/data/kaufland_discounts_2025-05-01.csv",
                "src/data/kaufland_discounts_2025-05-08.csv"
        );

        for (String store : stores) {
            for (String discountFile : discountFiles)
                allDiscountProducts.addAll(csvParserService.parseDiscounts(discountFile, store));
        }
        return allDiscountProducts;
    }

    public Optional<DiscountProduct> findActiveDiscountForProduct(Product product, List<DiscountProduct> discounts) {
        return discounts.stream()
                .filter(d ->
                        d.getProductId().equals(product.getProductId()) &&
                                d.getStore().equalsIgnoreCase(product.getStore()) &&
                                !product.getDate().isBefore(d.getFromDate()) &&
                                !product.getDate().isAfter(d.getToDate())
                )
                .findFirst();
    }

    public List<Product> filterProducts(String productName, String category, String storeFilter) throws IOException {
        List<Product> filteredProducts = findByProductName(productName);
        return filteredProducts.stream()
                .filter(p -> p.getProductName().equalsIgnoreCase(productName))
                .filter(p -> category == null || p.getProductCategory().equalsIgnoreCase(category))
                .filter(p -> storeFilter == null || p.getStore().equalsIgnoreCase(storeFilter))
                .collect(Collectors.toList());
    }

    private LocalDate extractDateFromFilename(String filename) {
        // Example: profi_2025-05-01.csv => 2025-05-01
        String dateStr = filename.replaceAll(".*_(\\d{4}-\\d{2}-\\d{2})\\.csv", "$1");
        return LocalDate.parse(dateStr);
    }
}
