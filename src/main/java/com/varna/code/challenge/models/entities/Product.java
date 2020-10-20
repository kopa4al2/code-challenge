package com.varna.code.challenge.models.entities;

import com.varna.code.challenge.models.binding.ProductBinding;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "products")
public class Product
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "created_date")
    private LocalDate dateCreated;

    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate;

    public Product()
    {
        this.dateCreated = LocalDate.now();
        this.lastModifiedDate = LocalDate.now();
        this.quantity = 1;
    }

    /**
     * Constructor used when creating new product entity from
     * front end binding model
     * @param binding the model from front end
     */
    public Product(ProductBinding binding)
    {
        this.category = binding.getCategory();
        this.name = binding.getName();
        this.description = binding.getDescription();
        this.dateCreated = LocalDate.now();
        this.lastModifiedDate = LocalDate.now();
        this.quantity = 1;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
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

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public LocalDate getDateCreated()
    {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    public LocalDate getLastModifiedDate()
    {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDate lastModifiedDate)
    {
        this.lastModifiedDate = lastModifiedDate;
    }
}
