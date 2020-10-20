package com.varna.code.challenge.models.view;

import com.varna.code.challenge.models.entities.Product;

import java.time.LocalDate;

public class ProductView
{
    private int id;

    private String name;

    private String category;

    private String description;

    private int quantity;

    private LocalDate dateCreated;

    private LocalDate dateModified;


    public ProductView()
    {
    }

    public ProductView(Product product)
    {
        this.id = product.getId();
        this.name = product.getName();
        this.category = product.getCategory();
        this.description = product.getDescription();
        this.quantity = product.getQuantity();
        this.dateCreated = product.getDateCreated();
        this.dateModified = product.getLastModifiedDate();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public LocalDate getDateCreated()
    {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDateModified()
    {
        return dateModified;
    }

    public void setDateModified(LocalDate dateModified)
    {
        this.dateModified = dateModified;
    }
}
