package com.wajahat.ordersaga.order.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wajahat.ordersaga.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    @Test
    void handlesBusinessException() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/boom"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ORDER_INVALID"));
    }

    @RestController
    private static class ThrowingController {
        @GetMapping("/boom")
        void boom() {
            throw new BusinessException("ORDER_INVALID", "Invalid order");
        }
    }
}
