package com.varna.code.challenge.services;

import com.varna.code.challenge.repositories.ProductRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(basePackages = "com.varna.code.challenge")
public abstract class AbstractProductServiceTests
{
    protected ProductService productService;

    @Mock
    protected ProductRepository productRepository;

    protected AbstractProductServiceTests()
    {
        MockitoAnnotations.initMocks(this);
        productService = new ProductServiceImpl(productRepository);
    }
}
