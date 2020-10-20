package com.varna.code.challenge.config;


import com.varna.code.challenge.models.entities.Product;
import com.varna.code.challenge.repositories.ProductRepository;
import com.varna.code.challenge.services.ProductService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Random;


/**
 * Fills 100 random products for testing
 */
@Configuration
public class DataLoaderConfig implements ApplicationListener<ApplicationReadyEvent>
{
    private static final String[] CATEGORIES = { "Laptop", "Computer", "Headset", "Monitor"};
    private final ProductRepository productRepository;

    public DataLoaderConfig(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent)
    {
        // Populate the database with dummy data
        if (productRepository.count() == 0) {
            Random rnd = new Random();
            for (int i = 0; i < 100; i++) {
                Product p = new Product();
                String randomCategory = CATEGORIES[rnd.nextInt(CATEGORIES.length)];
                p.setQuantity(rnd.nextInt(1000));
                p.setCategory(randomCategory);
                p.setName("Product" + (i + 1));
                p.setDateCreated(i % 2 == 0 ? LocalDate.now().minusDays(i) : LocalDate.now().plusDays(i));
                p.setLastModifiedDate(i % 2 == 0 ? LocalDate.now().plusDays(i % 3) : LocalDate.now().plusDays(i % 5));
                p.setDescription("Some description for " + p.getName() + " With category " + p.getCategory());
                productRepository.save(p);
            }
        }

    }
}
