package com.example.pricecomparator;

import com.example.pricecomparator.model.DiscountProduct;
import com.example.pricecomparator.repository.CsvDataRepository;
import com.example.pricecomparator.service.BestDiscountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class BestDiscountServiceTest {

    @Mock
    private CsvDataRepository csvDataRepository;

    @InjectMocks
    private BestDiscountService bestDiscountService;

    private final LocalDate today = LocalDate.of(2025, 5, 23);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBestDiscounts() throws IOException {
        DiscountProduct d1 = new DiscountProduct();
        d1.setProductId("P001");
        d1.setStore("lidl");
        d1.setPercentageOfDiscount(15);
        d1.setFromDate(today.minusDays(2));
        d1.setToDate(today.plusDays(2));

        DiscountProduct d2 = new DiscountProduct();
        d2.setProductId("P002");
        d2.setStore("profi");
        d2.setPercentageOfDiscount(25);
        d2.setFromDate(today.minusDays(1));
        d2.setToDate(today.plusDays(1));

        DiscountProduct d3 = new DiscountProduct();
        d3.setProductId("P003");
        d3.setStore("kaufland");
        d3.setPercentageOfDiscount(10);
        d3.setFromDate(today.minusDays(5));
        d3.setToDate(today.minusDays(1)); // Expired

        when(csvDataRepository.loadAllDiscounts(today)).thenReturn(List.of(d1, d2, d3));

        List<DiscountProduct> best = bestDiscountService.getBestDiscounts(2, today);

        assertEquals(2, best.size());
        assertEquals("P002", best.get(0).getProductId());
        assertEquals("P001", best.get(1).getProductId());
    }
}
