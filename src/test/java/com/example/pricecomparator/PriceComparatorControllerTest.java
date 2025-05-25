package com.example.pricecomparator;

import com.example.pricecomparator.controller.PriceComparatorController;
import com.example.pricecomparator.model.*;
import com.example.pricecomparator.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceComparatorController.class)
public class PriceComparatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BasketOptimizerService basketOptimizerService;
    @MockBean
    private BestDiscountService bestDiscountService;
    @MockBean
    private NewDiscountService newDiscountService;
    @MockBean
    private PriceHistoryService priceHistoryService;
    @MockBean
    private RecommendationService recommendationService;

    private final LocalDate today = LocalDate.of(2025, 5, 8);

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testGetBestDiscounts() throws Exception {
        DiscountProduct discount = new DiscountProduct();
        discount.setProductId("P001");
        discount.setStore("lidl");
        discount.setPercentageOfDiscount(10);
        discount.setFromDate(today);
        discount.setToDate(today.plusDays(1));

        Mockito.when(bestDiscountService.getBestDiscounts(1, today)).thenReturn(List.of(discount));

        mockMvc.perform(get("/api/prices/best-discounts")
                        .param("limit", "1")
                        .param("date", today.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("P001"));
    }

    @Test
    public void testGetNewDiscounts() throws Exception {
        DiscountProduct discount = new DiscountProduct();
        discount.setProductId("P002");
        discount.setStore("profi");
        discount.setFromDate(today);
        discount.setToDate(today.plusDays(1));
        discount.setPercentageOfDiscount(15);

        Mockito.when(newDiscountService.getNewlyAddedDiscounts(today)).thenReturn(List.of(discount));

        mockMvc.perform(get("/api/prices/new-discounts")
                        .param("date", today.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("P002"));
    }

    @Test
    public void testGetPriceHistory() throws Exception {
        PriceHistoryPoint point = new PriceHistoryPoint();
        point.setProductId("P001");
        point.setProductName("lapte");
        point.setStore("lidl");
        point.setDate(today);
        point.setPrice(10.0);
        point.setDiscount(10);
        point.setDiscountedPrice(9.0);

        Mockito.when(priceHistoryService.getPriceHistoryByProductName("lapte", null, "lidl"))
                .thenReturn(List.of(point));

        mockMvc.perform(get("/api/prices/price-history")
                        .param("productName", "lapte")
                        .param("store", "lidl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].discountedPrice").value(9.0));
    }

    @Test
    public void testGetRecommendations() throws Exception {
        ProductRecommendation rec = new ProductRecommendation();
        rec.setProductName("iaurt olympus");
        rec.setStore("kaufland");
        rec.setValuePerUnit(12.5);

        Mockito.when(recommendationService.findSubstitutesByProductName("iaurt", today))
                .thenReturn(List.of(rec));

        mockMvc.perform(get("/api/prices/recommendations")
                        .param("productName", "iaurt")
                        .param("date", today.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("iaurt olympus"));
    }
}
