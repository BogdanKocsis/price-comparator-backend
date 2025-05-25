package com.example.pricecomparator.service;

import com.example.pricecomparator.repository.CsvDataRepository;
import com.example.pricecomparator.model.DiscountProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class NewDiscountService {

    @Autowired
    private CsvDataRepository csvDataRepository;

    public List<DiscountProduct> getNewlyAddedDiscounts(LocalDate date) throws IOException {
        List<DiscountProduct> discountProducts = csvDataRepository.loadAllDiscounts(date);

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        Set<LocalDate> validDates = Set.of(today, yesterday);

        return discountProducts.stream()
                .filter(d -> validDates.contains(d.getFromDate()))
                .sorted(Comparator.comparing(DiscountProduct::getStore).
                        thenComparing(Comparator.comparingInt(DiscountProduct::getPercentageOfDiscount).reversed())
                )
                .toList();
    }

}
