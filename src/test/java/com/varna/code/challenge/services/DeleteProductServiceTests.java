package com.varna.code.challenge.services;

import com.varna.code.challenge.exceptions.ProductException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class DeleteProductServiceTests extends AbstractProductServiceTests
{

    @Test
    public void deleteWithInvalidId_ShouldThrow() throws Exception
    {
        final int invalidProductId = 122;
        when(productRepository.existsById(invalidProductId)).thenReturn(false);

        final String expectedExceptionMessage = "No such product";

        try {
            productService.deleteProduct(invalidProductId);
        } catch (Exception e) {
            assertThat(e, instanceOf(ProductException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

}
