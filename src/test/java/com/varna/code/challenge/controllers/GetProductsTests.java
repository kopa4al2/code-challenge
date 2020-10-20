package com.varna.code.challenge.controllers;

import com.varna.code.challenge.models.view.ProductView;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetProductsTests extends AbstractProductControllerTest
{
    private static final String GET_PRODUCTS_URL = "/products";


    @BeforeEach
    public void setup()
    {
        // Return empty page to not have NPE
        when(productRepository.findAll(any(Pageable.class)))
                .thenAnswer(arg -> new PageImpl<ProductView>(new ArrayList<>()));
    }

    @Test
    public void getProducts_WithIncorrectPageNumberOrPageSize_ShouldThrow() throws Exception
    {
        mockRequest.performGet(GET_PRODUCTS_URL, Map.of(
                "page", "string not allowed", "pageSize", "string not allowed"))
                .andExpect(status().isBadRequest());

        mockRequest.performGet(GET_PRODUCTS_URL, Map.of(
                "page", -1, "pageSize", 20))
                .andExpect(status().isBadRequest());

        mockRequest.performGet(GET_PRODUCTS_URL, Map.of(
                "page", 5, "pageSize", 0))
                .andExpect(status().isBadRequest());

        mockRequest.performGet(GET_PRODUCTS_URL, Map.of(
                "page", 0, "pageSize", -5))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getProducts_WithCorrectPageNumberAndPageSize_ShouldReturn200Ok() throws Exception
    {
        mockRequest.performGet(GET_PRODUCTS_URL, Map.of(
                "page", 1, "pageSize", 10))
                .andExpect(status().isOk());

        mockRequest.performGet(GET_PRODUCTS_URL, Map.of(
                "page", 0, "pageSize", 100))
                .andExpect(status().isOk());
    }

    @Test
    public void getProducts_WithCorrectPageNumberAndPageSizeAndSort_ShouldReturn200Ok() throws Exception
    {
        mockRequest.performGet(GET_PRODUCTS_URL, Map.of(
                "page", 1, "pageSize", 10,
                "orderBy", "name", "direction", "ASC"))
                .andExpect(status().isOk());
    }

    @Test
    public void getProducts_WithCorrectPageNumberAndPageSizeAndIncorrectSort_ShouldReturnBadRequest() throws Exception
    {
        mockRequest.performGet(GET_PRODUCTS_URL, Map.of(
                "page", 1, "pageSize", 10,
                 "direction", "ASC"))
                .andExpect(status().isBadRequest());

        mockRequest.performGet(GET_PRODUCTS_URL, Map.of(
                "page", 1, "pageSize", 10,
                "orderBy", "category"))
                .andExpect(status().isBadRequest());
    }
}
