package com.varna.code.challenge.services;

import com.varna.code.challenge.exceptions.ProductException;
import com.varna.code.challenge.models.binding.EditProductBinding;
import com.varna.code.challenge.models.entities.Product;
import com.varna.code.challenge.models.view.ProductView;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UpdateProductServiceTests extends AbstractProductServiceTests
{

    @Test
    public void updateWithWrongProductId_ShouldThrow() throws Exception
    {
        final int invalidProductId = 122;
        final String expectedExceptionMessage = "There is no product with such id";

        when(productRepository.findById(invalidProductId)).thenReturn(Optional.empty());

        EditProductBinding model = new EditProductBinding();
        try {
            productService.updateProduct(invalidProductId, model);
        } catch (Exception e) {
            assertThat(e, instanceOf(ProductException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void addWithInvalidFieldSize_ShouldThrow() throws Exception
    {
        final int productId = 1;
        final String expectedExceptionMessage = "You inserted too long values, name and category cannot exceed 16 symbols";

        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));

        EditProductBinding model = new EditProductBinding("".repeat(17), "".repeat(17), "");
        try {
            productService.updateProduct(1, model);
        } catch (Exception e) {
            assertThat(e, instanceOf(ProductException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void addWithCorrectFields_ShouldUpdateProduct() throws Exception
    {
        final int productId = 1;
        final String expectedName = "updated name";
        final String expectedCategory = "updated category";
        final String expectedDescription = "updated description";

        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));
        when(productRepository.save(any(Product.class))).thenAnswer(a -> a.getArgument(0));

        EditProductBinding model = new EditProductBinding(expectedName, expectedCategory, expectedDescription);

        ProductView actualProduct = productService.updateProduct(productId, model);

        assertEquals(expectedName, actualProduct.getName());
        assertEquals(expectedCategory, actualProduct.getCategory());
        assertEquals(expectedDescription, actualProduct.getDescription());
    }

    @Test
    public void addWithCorrectFields_ShouldUpdateLastModifiedDate() throws Exception
    {
        final int productId = 1;

        Product productWithOldLastModified = new Product();
        productWithOldLastModified.setLastModifiedDate(LocalDate.of(2020, 10, 15));

        when(productRepository.findById(productId)).thenReturn(Optional.of(productWithOldLastModified));
        when(productRepository.save(any(Product.class))).thenAnswer(a -> a.getArgument(0));

        EditProductBinding model = new EditProductBinding("name", "category", "");

        ProductView actualProduct = productService.updateProduct(productId, model);

        assertEquals(LocalDate.now(), actualProduct.getDateModified());
    }
}

