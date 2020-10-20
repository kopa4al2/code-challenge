package com.varna.code.challenge.services;

import com.varna.code.challenge.exceptions.ProductException;
import com.varna.code.challenge.models.entities.Product;
import com.varna.code.challenge.models.view.ProductView;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class OrderProductServiceTests extends AbstractProductServiceTests
{
    @Test
    public void orderProduct_WithNegativeAmount_ShouldThrow() throws Exception
    {
        final String expectedExceptionMessage = "Amount should be positive, greater than zero";
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(new Product()));

        try {
            productService.orderProduct(-1, 9);
        } catch (Exception e) {
            assertThat(e, instanceOf(ProductException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void orderProduct_WithNonExistingProduct_ShouldThrow() throws Exception
    {
        final String expectedExceptionMessage = "No such product";
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        try {
            productService.orderProduct(10, 9);
        } catch (Exception e) {
            assertThat(e, instanceOf(ProductException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void orderProduct_WithProductWithoutNeededAmount_ShouldThrow() throws Exception
    {
        final String expectedExceptionMessage = "There isn't that much in stock";
        final Product product = new Product();
        product.setQuantity(10);

        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));

        try {
            productService.orderProduct(11, 9);
        } catch (Exception e) {
            assertThat(e, instanceOf(ProductException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void orderProduct_WithCorrectAmount_ShouldDecreaseAmount() throws Exception
    {
        final int initialQuantity = 10;
        final int orderQuantity = 5;
        final Product product = new Product();
        product.setQuantity(initialQuantity);

        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(a -> a.getArgument(0));


        ProductView actualProduct = productService.orderProduct(orderQuantity, 9);

        assertEquals(initialQuantity - orderQuantity, actualProduct.getQuantity());
    }
}

