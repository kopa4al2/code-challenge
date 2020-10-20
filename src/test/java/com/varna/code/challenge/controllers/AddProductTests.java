package com.varna.code.challenge.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.varna.code.challenge.models.binding.ProductBinding;
import com.varna.code.challenge.models.entities.Product;
import com.varna.code.challenge.models.view.ProductView;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AddProductTests extends AbstractProductControllerTest
{
    private static final String ADD_PRODUCT_URL = "/products/new";


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
    public void addWithCorrectProduct_ShouldReturnTheProductViewModel() throws Exception
    {
        when(productRepository.save(any(Product.class)))
                .thenAnswer(p -> p.getArgument(0));

        final String expectedName = "name";
        final String expectedCategory = "category";
        final String expectedDescription = "description";

        ProductBinding model = new ProductBinding(expectedCategory, expectedName, expectedDescription);
        String response = mockRequest.performPost(ADD_PRODUCT_URL, model)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ProductView expectedResponse = parseResponseBody(response);

        final String actualName = expectedResponse.getName();
        final String actualCategory = expectedResponse.getCategory();
        final String actualDescription = expectedResponse.getDescription();

        assertNotNull(expectedResponse);
        assertEquals(expectedName, actualName);
        assertEquals(expectedCategory, actualCategory);
        assertEquals(expectedDescription, actualDescription);
    }

    @Test
    public void addWithCorrectProduct_ShouldReturnProductWithCorrectQuantityAndDate() throws Exception
    {
        when(productRepository.save(any(Product.class)))
                .thenAnswer(p -> p.getArgument(0));


        ProductBinding model = new ProductBinding("not empty name", "not empty category", "");
        String response = mockRequest.performPost(ADD_PRODUCT_URL, model)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ProductView expectedResponse = parseResponseBody(response);

        final int actualQuantity = expectedResponse.getQuantity();
        final LocalDate actualDateCreated = expectedResponse.getDateCreated();
        final LocalDate actualDateModified = expectedResponse.getDateModified();

        assertNotNull(expectedResponse);
        assertEquals(1, actualQuantity);
        assertEquals(LocalDate.now(), actualDateCreated);
        assertEquals(LocalDate.now(), actualDateModified);
    }


//    @Test
//    public void addExistingProduct_ShouldIncreaseQuantity_AndUpdateLastModifiedDate() throws Exception
//    {
//        when(productRepository.save(any(Product.class)))
//                .thenAnswer(p -> {
//                    Product returnedProduct = p.getArgument(0);
//                    returnedProduct.setLastModifiedDate(LocalDate.now().minusDays(2));
//                    // Make mockito return product with last modified date being 2 days ago
//                    return returnedProduct;
//                });
//
//        ProductBinding model = new ProductBinding("existing", "category", "description");
//        mockRequest.performPost(ADD_PRODUCT_URL, model);
//
//
//        // Make mockito return the actual product again
//        when(productRepository.save(any(Product.class)))
//                .thenAnswer(p ->  p.getArgument(0));
//
//        ProductBinding existingProduct = new ProductBinding("existing", "different category", "different description");
//        String response = mockRequest.performPost(ADD_PRODUCT_URL, existingProduct)
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        ProductView responseProduct = parseResponseBody(response);
//
//        final int actualQuantity = responseProduct.getQuantity();
//        final LocalDate actualDateModified = responseProduct.getDateModified();
//
//        assertEquals(2, actualQuantity);
//        assertEquals(LocalDate.now(), actualDateModified);
//    }

    private ProductView parseResponseBody(String response) throws JsonProcessingException
    {
        JsonNode root = objectMapper.readValue(response, JsonNode.class);
        return objectMapper.readValue(root.get("body").toString(), ProductView.class);
    }
}
