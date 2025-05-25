package com.example.pricecomparator;

import com.example.pricecomparator.model.BestPriceResult;
import com.example.pricecomparator.model.DiscountProduct;
import com.example.pricecomparator.model.Product;
import com.example.pricecomparator.repository.CsvDataRepository;
import com.example.pricecomparator.service.BasketOptimizerService;
import com.example.pricecomparator.service.PriceComparisonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class BasketOptimizerServiceTest {

    @Mock
    private PriceComparisonService priceComparisonService;

    @Mock
    private CsvDataRepository csvDataRepository;

    @InjectMocks
    private BasketOptimizerService basketOptimizerService;

    private final LocalDate today = LocalDate.of(2025, 5, 23);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testOptimizeShoppingList() throws IOException {
        Product p1 = new Product();
        p1.setProductId("P001");
        p1.setProductName("lapte");
        p1.setStore("lidl");
        p1.setDate(today);

        Product p2 = new Product();
        p2.setProductId("P002");
        p2.setProductName("paine");
        p2.setStore("profi");
        p2.setDate(today);

        DiscountProduct d1 = new DiscountProduct();
        d1.setProductId("P001");
        d1.setStore("lidl");
        d1.setFromDate(today.minusDays(1));
        d1.setToDate(today.plusDays(1));
        d1.setPercentageOfDiscount(10);

        DiscountProduct d2 = new DiscountProduct();
        d2.setProductId("P002");
        d2.setStore("profi");
        d2.setFromDate(today.minusDays(1));
        d2.setToDate(today.plusDays(1));
        d2.setPercentageOfDiscount(5);

        BestPriceResult r1 = new BestPriceResult();
        r1.setProductName("lapte");
        r1.setStore("lidl");

        BestPriceResult r2 = new BestPriceResult();
        r2.setProductName("paine");
        r2.setStore("profi");

        List<String> basket = List.of("lapte", "paine");

        when(csvDataRepository.loadAllProducts(today)).thenReturn(List.of(p1, p2));
        when(csvDataRepository.loadAllDiscounts(today)).thenReturn(List.of(d1, d2));
        when(priceComparisonService.getBestPrices(csvDataRepository.loadAllProducts(today), csvDataRepository.loadAllDiscounts(today))).thenReturn(List.of(r1, r2));

        Map<String, List<BestPriceResult>> optimized = basketOptimizerService.optimizeShoppingList(basket, today);

        assertEquals(2, optimized.size());
        assertTrue(optimized.containsKey("lidl"));
        assertTrue(optimized.containsKey("profi"));
        assertEquals(1, optimized.get("lidl").size());
        assertEquals(1, optimized.get("profi").size());
    }

}
