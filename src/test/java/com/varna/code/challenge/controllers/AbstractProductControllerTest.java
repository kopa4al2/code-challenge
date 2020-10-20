package com.varna.code.challenge.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.varna.code.challenge.repositories.ProductRepository;
import com.varna.code.challenge.services.ProductServiceTests;
import com.varna.code.challenge.utils.MockRequest;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

@WebMvcTest(controllers = ProductController.class)
@ContextConfiguration(classes = { MockRequest.class })
@ComponentScan(basePackages = "com.varna.code.challenge")
public abstract class AbstractProductControllerTest
{
    @Autowired
    protected MockRequest mockRequest;

    @MockBean
    protected ProductRepository productRepository;

    @Autowired
    protected ProductServiceTests productService;

    @Autowired
    protected ObjectMapper objectMapper;

    protected AbstractProductControllerTest()
    {
        MockitoAnnotations.initMocks(this);
    }
}
