package com.example.pricecomparator.service;

import com.example.pricecomparator.model.DiscountProduct;
import com.example.pricecomparator.model.Product;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParserService {

    public List<Product> parseProducts(String filePath, String store, LocalDate date) throws IOException {
        List<Product> products = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {
            reader.readNext();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Product product = new Product();
                product.setProductId(nextLine[0]);
                product.setProductName(nextLine[1]);
                product.setProductCategory(nextLine[2]);
                product.setBrand(nextLine[3]);
                product.setPackageQuantity(Double.parseDouble(nextLine[4]));
                product.setPackageUnit(nextLine[5]);
                product.setPrice(Double.parseDouble(nextLine[6]));
                product.setCurrency(nextLine[7]);
                product.setDate(date);
                product.setStore(store);
                products.add(product);
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public List<DiscountProduct> parseDiscounts(String filePath, String store) throws IOException {
        List<DiscountProduct> discountProducts = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {
            reader.readNext();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                DiscountProduct discountProduct = new DiscountProduct();
                discountProduct.setProductId(nextLine[0]);
                discountProduct.setProductName(nextLine[1]);
                discountProduct.setBrand(nextLine[2]);
                discountProduct.setPackageQuantity(Double.parseDouble(nextLine[3]));
                discountProduct.setPackageUnit(nextLine[4]);
                discountProduct.setProductCategory(nextLine[5]);
                discountProduct.setFromDate(LocalDate.parse(nextLine[6]));
                discountProduct.setToDate(LocalDate.parse(nextLine[7]));
                discountProduct.setPercentageOfDiscount(Integer.parseInt(nextLine[8]));
                discountProduct.setStore(store);
                discountProducts.add(discountProduct);
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return discountProducts;
    }

}
