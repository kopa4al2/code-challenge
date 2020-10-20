package com.varna.code.challenge.models.binding;

/**
 * Binding model representing a PUT request to update a product
 * contains only editable fields
 */
public class EditProductBinding
{
    private String description;

    private String name;

    private String category;

    public EditProductBinding()
    {
    }

    public EditProductBinding(String name, String category, String description)
    {
        this.description = description;
        this.name = name;
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
}
