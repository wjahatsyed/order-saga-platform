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

    @Test
    void handlesResourceNotFoundException() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void handlesGenericException() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));
    }

    @Test
    void handlesValidationException() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/validate"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("field: default message"));
    }

    @Test
    void handlesResourceNotFoundBusinessException() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/business-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @RestController
    private static class ThrowingController {
        @GetMapping("/boom")
        void boom() {
            throw new BusinessException("ORDER_INVALID", "Invalid order");
        }

        @GetMapping("/business-not-found")
        void businessNotFound() {
            throw new BusinessException("RESOURCE_NOT_FOUND", "Resource not found");
        }

        @GetMapping("/not-found")
        void notFound() {
            throw new com.wajahat.ordersaga.common.exception.ResourceNotFoundException("Not found");
        }

        @GetMapping("/error")
        void error() {
            throw new RuntimeException("Unexpected");
        }

        @GetMapping("/validate")
        void validate() throws NoSuchMethodException, org.springframework.web.bind.MethodArgumentNotValidException {
            org.springframework.validation.BindingResult bindingResult = new org.springframework.validation.BeanPropertyBindingResult(new Object(), "object");
            bindingResult.addError(new org.springframework.validation.FieldError("object", "field", "default message"));
            
            java.lang.reflect.Method method = ThrowingController.class.getDeclaredMethod("validate");
            org.springframework.core.MethodParameter methodParameter = new org.springframework.core.MethodParameter(method, -1);
            
            throw new org.springframework.web.bind.MethodArgumentNotValidException(methodParameter, bindingResult);
        }
    }
}
