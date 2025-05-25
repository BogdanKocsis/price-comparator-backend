package com.example.pricecomparator.service;

import com.example.pricecomparator.model.DiscountProduct;
import com.example.pricecomparator.model.PriceAlert;
import com.example.pricecomparator.model.Product;
import com.example.pricecomparator.repository.CsvDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class PriceAlertService {

    @Autowired
    private CsvDataRepository csvDataRepository;

    private final List<PriceAlert> savedAlerts = new CopyOnWriteArrayList<>();

    public void saveAlert(PriceAlert alert) {
        savedAlerts.add(alert);
    }

    public List<PriceAlert> getAlerts() {
        return savedAlerts;
    }

    @Scheduled(cron = "0 0 09 * * ?")
    public void runPriceAlertChecks() throws IOException {
        LocalDate date = LocalDate.of(2025, Month.MAY, 8);
        List<Product> products = csvDataRepository.loadAllProducts(date);
        List<DiscountProduct> discountProducts = csvDataRepository.loadAllOverDiscounts();

        for (PriceAlert alert : savedAlerts) {
            for (Product product : products) {
                if (!product.getProductName().equalsIgnoreCase(alert.getProductName())) continue;

                double price = product.getPrice();
                Optional<DiscountProduct> discount = csvDataRepository.findActiveDiscountForProduct(product, discountProducts);

                if (discount.isPresent()) {
                    price -= price * discount.get().getPercentageOfDiscount() / 100;
                }

                if (price <= alert.getTargetPrice()) {
                    System.out.printf("[ALERT] %s at %s: %.2f RON <= %.2f RON\n",
                            product.getProductName(), product.getStore(), price, alert.getTargetPrice());
                }

            }

        }
    }
}