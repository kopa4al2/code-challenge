package com.varna.code.challenge.repositories;

import com.varna.code.challenge.models.entities.Product;
import com.varna.code.challenge.models.view.ProductsByCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
@Transactional
public class ProductRepositoryTests
{

    @Autowired
    private ProductRepository productRepository;

    private static final String categoryOne = "categoryOne";

    private static final String categoryTwo = "categoryTwo";

    private static final String product1 = "product1";

    private static final String product2 = "product2";

    private static final String product3 = "product3";

    private static final String product4 = "product4";

    private static final String product5 = "product5";

    @Test
    public void getAllCategories_ShouldReturnCorrect()
    {
        productRepository.saveAll(createInitialData());

        List<ProductsByCategory> actualResult = productRepository.getProductsByCategory();

        assertNotNull(actualResult);
        assertEquals(2, actualResult.size());

        Map<String, Long> mappedByCategory = actualResult.stream()
                .collect(Collectors.toMap(
                        ProductsByCategory::getCategory, ProductsByCategory::getProductsAvailable));

        assertEquals(2L, mappedByCategory.get(categoryOne));
        assertEquals(3L, mappedByCategory.get(categoryTwo));
    }

    private List<Product> createInitialData()
    {
        Product p1 = new Product();
        Product p2 = new Product();
        Product p3 = new Product();
        Product p4 = new Product();
        Product p5 = new Product();

        p1.setName(product1);
        p1.setCategory(categoryOne);

        p2.setName(product2);
        p2.setCategory(categoryOne);

        p3.setName(product3);
        p3.setCategory(categoryTwo);

        p4.setName(product4);
        p4.setCategory(categoryTwo);

        p5.setName(product5);
        p5.setCategory(categoryTwo);

        return List.of(p1, p2, p3, p4, p5);
    }
}
