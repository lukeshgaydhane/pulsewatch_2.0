package com.pulsewatch.dashboard.controller;

import com.pulsewatch.dashboard.service.DashboardService;
import com.pulsewatch.dashboard.service.DatabaseService;
import com.pulsewatch.dashboard.entity.DashboardMetrics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private DatabaseService databaseService;

    @Test
    void testGetDashboardSummary() throws Exception {
        // Given
        Map<String, Object> mockSummary = new HashMap<>();
        mockSummary.put("userId", 1L);
        mockSummary.put("alerts", new Object[]{});
        mockSummary.put("predictions", new HashMap<>());
        mockSummary.put("config", new HashMap<>());

        List<Map<String, Object>> mockDbMetrics = new ArrayList<>();

        when(dashboardService.getDashboardSummary(eq(1L))).thenReturn(mockSummary);
        when(databaseService.getDashboardSummaryFromDB(eq(1L))).thenReturn(mockDbMetrics);

        // When & Then
        mockMvc.perform(get("/dashboard/summary")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void testGetHealth() throws Exception {
        // When & Then
        mockMvc.perform(get("/dashboard/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("dashboard-service"));
    }
} 