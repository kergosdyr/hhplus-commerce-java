package kr.hhplus.be.server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.WebIntegrationTest;
import kr.hhplus.be.server.util.MockJwt;

class ProductControllerWebIntegrationTest extends WebIntegrationTest {

    @Test
    @DisplayName("GET /api/v1/products 로 요청 시 Mocking된 성공 응답을 반환한다")
    void getProducts() throws Exception {

        mockMvc.perform(
                get("/api/v1/products?page=1&size=10").header("Authorization", "Bearer " + MockJwt.MOCK.getToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.items[0].id").value(1))
            .andExpect(jsonPath("$.data.items[0].name").value("Apple iPad"))
            .andExpect(jsonPath("$.data.items[0].price").value(500000))
            .andExpect(jsonPath("$.data.items[0].stock").value(100))
            .andExpect(jsonPath("$.data.items[1].id").value(2))
            .andExpect(jsonPath("$.data.items[1].name").value("Samsung Galaxy Tab"))
            .andExpect(jsonPath("$.data.items[1].price").value(400000))
            .andExpect(jsonPath("$.data.items[1].stock").value(50))
            .andExpect(jsonPath("$.data.pageInfo.currentPage").value(1))
            .andExpect(jsonPath("$.data.pageInfo.totalPages").value(5))
            .andExpect(jsonPath("$.data.pageInfo.totalItems").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/products/top-sellers 로 요청 시 Mocking된 성공 응답을 반환한다")
    void getTopSellers() throws Exception {

        mockMvc.perform(
                get("/api/v1/products/top-sellers?days=3").header("Authorization", "Bearer " + MockJwt.MOCK.getToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.periodDays").value(3))
            .andExpect(jsonPath("$.data.topSellers[0].id").value(1))
            .andExpect(jsonPath("$.data.topSellers[0].name").value("Apple iPad"))
            .andExpect(jsonPath("$.data.topSellers[0].totalSold").value(150))
            .andExpect(jsonPath("$.data.topSellers[1].id").value(5))
            .andExpect(jsonPath("$.data.topSellers[1].name").value("MacBook Air"))
            .andExpect(jsonPath("$.data.topSellers[1].totalSold").value(100))
            .andExpect(jsonPath("$.data.topSellers[2].id").value(2))
            .andExpect(jsonPath("$.data.topSellers[2].name").value("Galaxy Tab"))
            .andExpect(jsonPath("$.data.topSellers[2].totalSold").value(80));
    }
}
