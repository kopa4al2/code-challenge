package com.varna.code.challenge.services;

import com.varna.code.challenge.exceptions.ProductException;
import com.varna.code.challenge.models.binding.EditProductBinding;
import com.varna.code.challenge.models.entities.Product;
import com.varna.code.challenge.models.view.ProductByPage;
import com.varna.code.challenge.models.view.ProductView;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * There are three methods (4 if we count the `getCount` one)
 * with similar (if not the same) logic
 * here i test only the one given in the assignment
 */
public class PaginationAndSortingProductServiceTests extends AbstractProductServiceTests
{

    @Test
    public void getAllWithIncorrectPageSizeAndNumber_ShouldThrow() throws Exception
    {
        final String expectedExceptionMessage = "Invalid page number or page size";

        try {
            productService.getProducts(-1, -1, null, null);
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void getAllWithIncorrectSortValues_ShouldThrow() throws Exception
    {
        final String expectedExceptionMessage = "You must either provide both orderBy and direction, or none";

        try {
            productService.getProducts(1, 11, "name", null);
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }

        try {
            productService.getProducts(1, 11, null, "ASC");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void getAllWithIncorrectProductFieldToSort_ShouldThrow() throws Exception
    {
        final String incorrectSortField = "not_existing_field";
        final String expectedExceptionMessage = "No such property to sort " + incorrectSortField;

        when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<Product>(new ArrayList<>()));

        try {
            productService.getProducts(1, 11, incorrectSortField, "asc");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void getAllWithIncorrectSortDirection_ShouldThrow() throws Exception
    {
        final String expectedExceptionMessage = "Sort order should be one of ASC or DESC (case insensitive)";

        when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<Product>(new ArrayList<>()));

        try {
            productService.getProducts(1, 11, "name", "as");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void getAllWithCorrectResponse_ShouldReturnCorrectModel() throws Exception
    {
        when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<Product>(new ArrayList<>()));


        ProductByPage actualReturnValue = productService.getProducts(1, 11, "name", "asc");

        assertNotNull(actualReturnValue);
    }

    @Test
    public void getAllWithCorrectResponse_ShouldReturnCorrectModelWithCorrectFields() throws Exception
    {
        List<Product> expectedList = List.of(new Product(), new Product());
        long expectedCount = 2L;
        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<Product>(expectedList));
        when(productRepository.count())
                .thenReturn(expectedCount);


        ProductByPage actualReturnValue = productService.getProducts(1, 11, "name", "asc");

        assertNotNull(actualReturnValue);
        assertEquals(expectedCount, actualReturnValue.getTotalRecords());
        assertEquals(expectedList.size(), actualReturnValue.getProducts().size());
    }
}

