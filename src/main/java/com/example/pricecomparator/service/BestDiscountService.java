package com.example.pricecomparator.service;


import com.example.pricecomparator.model.DiscountProduct;
import com.example.pricecomparator.repository.CsvDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class BestDiscountService {

    @Autowired
    private CsvDataRepository csvDataRepository;

    public List<DiscountProduct> getBestDiscounts(int limit, LocalDate date) throws IOException {
        List<DiscountProduct> discountProducts = csvDataRepository.loadAllDiscounts(date);

        return discountProducts.stream()
                .filter(d -> !LocalDate.now().isBefore(d.getFromDate()) && !LocalDate.now().isAfter(d.getToDate()))
                .sorted(Comparator.comparingInt(DiscountProduct::getPercentageOfDiscount).reversed())
                .limit(limit)
                .toList();
    }

}
