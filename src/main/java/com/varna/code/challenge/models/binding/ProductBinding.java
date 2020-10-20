package com.varna.code.challenge.models.binding;

/**
 * Model with necessary fields for inserting new products to the database
 */
public class ProductBinding
{
    private String category;

    private String name;

    private String description;

    public ProductBinding(String category, String name, String description)
    {
        this.category = category;
        this.name = name;
        this.description = description;
    }

    public ProductBinding()
    {
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
