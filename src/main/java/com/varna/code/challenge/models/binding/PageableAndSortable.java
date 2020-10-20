package com.varna.code.challenge.models.binding;

import java.util.Map;

public class PageableAndSortable
{
    private int pageNumber;

    private int itemsPerPage;

    /**
     * Map with key being the property and the value being the sort order (desc or asc)
     */
    private Map<String, SortOrder> sortedProperties;

    public PageableAndSortable()
    {
    }

    public int getPageNumber()
    {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber)
    {
        this.pageNumber = pageNumber;
    }

    public int getItemsPerPage()
    {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage)
    {
        this.itemsPerPage = itemsPerPage;
    }

    public Map<String, SortOrder> getSortedProperties()
    {
        return sortedProperties;
    }

    public void setSortedProperties(Map<String, SortOrder> sortedProperties)
    {
        this.sortedProperties = sortedProperties;
    }
}
