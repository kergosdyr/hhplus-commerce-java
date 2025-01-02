package kr.hhplus.be.server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import kr.hhplus.be.server.config.WebIntegrationTest;
import kr.hhplus.be.server.util.MockJwt;

class OrderControllerWebIntegrationTest extends WebIntegrationTest {

    @Test
    @DisplayName("POST /api/v1/order 로 요청 시 Mocking된 성공 응답을 반환한다")
    void createOrder() throws Exception {

        mockMvc.perform(
                post("/api/v1/order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "userId": 1
                            }
                        """).header("Authorization", "Bearer " + MockJwt.MOCK.getToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.orderId").value(20240001))
            .andExpect(jsonPath("$.data.userId").value(1))
            .andExpect(jsonPath("$.data.status").value("PAID"))
            .andExpect(jsonPath("$.data.totalAmount").value(150000))
            .andExpect(jsonPath("$.data.discountAmount").value(15000))
            .andExpect(jsonPath("$.data.paidAmount").value(135000))
            .andExpect(jsonPath("$.data.paymentId").value(50001))
            .andExpect(jsonPath("$.data.paymentStatus").value("SUCCESS"))
            .andExpect(jsonPath("$.data.orderItems[0].productId").value(1))
            .andExpect(jsonPath("$.data.orderItems[0].quantity").value(2))
            .andExpect(jsonPath("$.data.orderItems[0].price").value(50000))
            .andExpect(jsonPath("$.data.orderItems[1].productId").value(2))
            .andExpect(jsonPath("$.data.orderItems[1].quantity").value(3))
            .andExpect(jsonPath("$.data.orderItems[1].price").value(20000))
            .andExpect(jsonPath("$.data.createdAt").value("2024-01-01T10:00:00"));
    }
}
