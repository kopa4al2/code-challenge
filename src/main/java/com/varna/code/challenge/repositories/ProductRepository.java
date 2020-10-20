package com.varna.code.challenge.repositories;

import com.varna.code.challenge.models.entities.Product;
import com.varna.code.challenge.models.view.ProductsByCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>
{
    Product findByName(String name);


    @Query("select new com.varna.code.challenge.models.view.ProductsByCategory(" +
            "p.category, count(p)) " +
            "from Product p " +
            "group by p.category")
    List<ProductsByCategory> getProductsByCategory();
}
