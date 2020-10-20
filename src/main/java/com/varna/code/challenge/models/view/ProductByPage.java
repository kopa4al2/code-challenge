package com.varna.code.challenge.models.view;

import java.util.List;

public class ProductByPage
{
    private long totalRecords;

    private List<ProductView> products;

    public ProductByPage()
    {
    }

    public ProductByPage(long totalRecords, List<ProductView> products)
    {
        this.totalRecords = totalRecords;
        this.products = products;
    }

    public long getTotalRecords()
    {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords)
    {
        this.totalRecords = totalRecords;
    }

    public List<ProductView> getProducts()
    {
        return products;
    }

    public void setProducts(List<ProductView> products)
    {
        this.products = products;
    }
}
