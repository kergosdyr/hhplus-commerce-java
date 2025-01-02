package kr.hhplus.be.server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.WebIntegrationTest;

class ProductControllerWebIntegrationTest extends WebIntegrationTest {

    @Test
    @DisplayName("GET /api/v1/products 로 요청 시 Mocking된 성공 응답을 반환한다")
    void getProducts() throws Exception {

        mockMvc.perform(get("/api/v1/products?page=1&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.items[0].productId").value(1))
            .andExpect(jsonPath("$.data.items[0].productName").value("Apple iPad"))
            .andExpect(jsonPath("$.data.items[0].price").value(500000))
            .andExpect(jsonPath("$.data.items[0].stockQuantity").value(100))
            .andExpect(jsonPath("$.data.items[1].productId").value(2))
            .andExpect(jsonPath("$.data.items[1].productName").value("Samsung Galaxy Tab"))
            .andExpect(jsonPath("$.data.items[1].price").value(400000))
            .andExpect(jsonPath("$.data.items[1].stockQuantity").value(50))
            .andExpect(jsonPath("$.data.pageInfo.currentPage").value(1))
            .andExpect(jsonPath("$.data.pageInfo.totalPages").value(5))
            .andExpect(jsonPath("$.data.pageInfo.totalItems").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/products/top-sellers 로 요청 시 Mocking된 성공 응답을 반환한다")
    void getTopSellers() throws Exception {

        mockMvc.perform(get("/api/v1/products/top-sellers?days=3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.periodDays").value(3))
            .andExpect(jsonPath("$.data.topSellers[0].productId").value(1))
            .andExpect(jsonPath("$.data.topSellers[0].productName").value("Apple iPad"))
            .andExpect(jsonPath("$.data.topSellers[0].totalSoldQuantity").value(150))
            .andExpect(jsonPath("$.data.topSellers[1].productId").value(5))
            .andExpect(jsonPath("$.data.topSellers[1].productName").value("MacBook Air"))
            .andExpect(jsonPath("$.data.topSellers[1].totalSoldQuantity").value(100))
            .andExpect(jsonPath("$.data.topSellers[2].productId").value(2))
            .andExpect(jsonPath("$.data.topSellers[2].productName").value("Galaxy Tab"))
            .andExpect(jsonPath("$.data.topSellers[2].totalSoldQuantity").value(80));
    }
}
