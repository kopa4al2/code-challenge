package com.varna.code.challenge.services;

import com.varna.code.challenge.exceptions.ProductException;
import com.varna.code.challenge.models.binding.ProductBinding;
import com.varna.code.challenge.models.entities.Product;
import com.varna.code.challenge.models.view.ProductView;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AddProductServiceTests extends AbstractProductServiceTests
{

    @Test
    public void addWithNullProductNameAndCategory_ShouldThrow() throws Exception
    {
        final String expectedExceptionMessage = "Invalid product passed, you need name and category";

        ProductBinding model = new ProductBinding();
        try {
            productService.addProduct(model);
        } catch (Exception e) {
            assertThat(e, instanceOf(ProductException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void addWithEmptyProductNameAndCategory_ShouldThrow() throws Exception
    {
        final String expectedExceptionMessage = "Invalid product passed, you need name and category";

        ProductBinding model = new ProductBinding("", "", "");
        try {
            productService.addProduct(model);
        } catch (Exception e) {
            assertThat(e, instanceOf(ProductException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void addWithExistingProductName_ShouldIncreaseQuantityAndDateModified() throws Exception
    {
        final String productName = "product1";
        when(productRepository.findByName(productName))
                .thenAnswer(a -> {
                    Product p = new Product();
                    p.setName(productName);
                    p.setLastModifiedDate(LocalDate.now().minusDays(2));
                    return p;
                });
        when(productRepository.save(any(Product.class)))
                .thenAnswer(a -> a.getArgument(0));

        ProductBinding model = new ProductBinding("correctCategory", productName, "");

        ProductView actualProduct = productService.addProduct(model);

        assertEquals(2, actualProduct.getQuantity());
        assertEquals(LocalDate.now(), actualProduct.getDateModified());

    }

}
