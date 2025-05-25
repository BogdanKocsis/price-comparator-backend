package com.example.pricecomparator;

import com.example.pricecomparator.controller.PriceAlertController;
import com.example.pricecomparator.model.PriceAlert;
import com.example.pricecomparator.service.PriceAlertService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceAlertController.class)
public class PriceAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceAlertService priceAlertService;

    @Test
    public void testSaveAlert() throws Exception {
        String alertJson = "{\"productName\": \"lapte zuzu\", \"targetPrice\": 8.5}";

        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alertJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAlerts() throws Exception {
        PriceAlert alert = new PriceAlert();
        alert.setProductName("lapte zuzu");
        alert.setTargetPrice(8.5);

        when(priceAlertService.getAlerts()).thenReturn(List.of(alert));

        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("lapte zuzu"))
                .andExpect(jsonPath("$[0].targetPrice").value(8.5));
    }
}