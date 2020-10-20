package com.varna.code.challenge.controllers;


import com.varna.code.challenge.models.binding.ProductBinding;
import com.varna.code.challenge.models.entities.Product;
import com.varna.code.challenge.models.view.ProductView;
import com.varna.code.challenge.repositories.ProductRepository;
import com.varna.code.challenge.utils.MockRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ProductController.class)
@ContextConfiguration(classes = {MockRequest.class})
@ComponentScan(basePackages = "com.varna.code.challenge")
public class ProductControllerTests
{
    private static final String ADD_PRODUCT_URL = "/products/new";

    private static final String GET_PRODUCTS_URL = "/products";

    @Autowired
    private MockRequest mockRequest;

    @MockBean
    private ProductRepository productRepository;

    public ProductControllerTests()
    {
        MockitoAnnotations.initMocks(this);
    }


    @BeforeEach
    public void setup()
    {
        // Return empty page to not have NPE
        when(productRepository.findAll(any(Pageable.class)))
                .thenAnswer(arg -> new PageImpl<ProductView>(new ArrayList<>()));
    }


    @Test
    public void addWithNullProductNameAndCategory_ShouldThrow() throws Exception
    {
        final String expectedStatusString = "FAILED";
        final String expectedErrorMessage = "Invalid product passed, you need name and category";

        ProductBinding model = new ProductBinding();
        mockRequest.performPost(ADD_PRODUCT_URL, model)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(expectedStatusString)))
                .andExpect(jsonPath("$.body", is(expectedErrorMessage)))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void addWithEmptyProductNameAndCategory_ShouldThrow() throws Exception
    {
        final String expectedStatusString = "FAILED";
        final String expectedErrorMessage = "Invalid product passed, you need name and category";

        ProductBinding model = new ProductBinding("", "", "");
        mockRequest.performPost(ADD_PRODUCT_URL, model)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(expectedStatusString)))
                .andExpect(jsonPath("$.body", is(expectedErrorMessage)))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void addWithCorrectProduct_ShouldReturn200Ok() throws Exception
    {
        when(productRepository.save(any(Product.class)))
                .thenAnswer(p -> p.getArgument(0));

        ProductBinding model = new ProductBinding("correctCategory", "correctName", "");
        mockRequest.performPost(ADD_PRODUCT_URL, model)
                .andExpect(status().isOk());
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
