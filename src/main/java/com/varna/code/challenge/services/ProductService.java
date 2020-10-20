package com.varna.code.challenge.services;

import com.varna.code.challenge.exceptions.ProductException;
import com.varna.code.challenge.models.binding.EditProductBinding;
import com.varna.code.challenge.models.binding.PageableAndSortable;
import com.varna.code.challenge.models.binding.ProductBinding;
import com.varna.code.challenge.models.view.ProductByPage;
import com.varna.code.challenge.models.view.ProductView;
import com.varna.code.challenge.models.view.ProductsByCategory;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ProductService
{
    /**
     * Insert a product to products table
     *
     * @param productToAdd the product binding model from front end
     * @return The product view model of the inserted product
     * @throws ProductException if there is another product in this category with the same name
     */
    ProductView addProduct(ProductBinding productToAdd) throws ProductException;

    /**
     * Deletes a product by id
     *
     * @param productId the id of the product
     * @throws ProductException if there is no such id
     */
    void deleteProduct(int productId) throws ProductException;


    /**
     * Updates a given product
     *
     * @param productId     the id of the product
     * @param productToEdit the product fields to update
     * @return the updated product
     * @throws ProductException if no product with such id or
     *                          if invalid arguments are passed (too long field, or wrong character)
     */
    ProductView updateProduct(int productId, EditProductBinding productToEdit) throws ProductException;

    /**
     * Get products by page with single sort
     *
     * @param pageNumber     the current page
     * @param pageSize       the items per page
     * @param orderBy        the order criteria
     * @param orderDirection the order direction
     * @return List of products for page and sort and total products
     * @throws IllegalArgumentException if orderBy is present by direction is not,
     *                          if invalid direction is passed
     *                          if invalid page number or page size is given
     *                          if there is no field 'orderBy' in products table
     */
    ProductByPage getProducts(int pageNumber, int pageSize, @Nullable String orderBy, @Nullable String orderDirection)
            throws IllegalArgumentException;

    /**
     * @return the amount of all products in the table
     */
    long getProductCount();

    /**
     * Returns list of products with size = itemsPerPage by page
     *
     * @param pageNumber   the number of page
     * @param itemsPerPage the items per page
     * @return the list of products for this page
     * @throws ProductException invalid pageNumber or invalid itemsPerPage
     */
    List<ProductView> getAllByPage(int pageNumber, int itemsPerPage) throws ProductException;

    /**
     * Returns list of products by page and sort
     *
     * @param pageableAndSortable the page and sort object to determine by which fields to sort
     * @return the list of products for the desired page and desired sort
     * @throws ProductException invalid sort argument, invalid page number, invalid page size
     */
    List<ProductView> getAllByPage(PageableAndSortable pageableAndSortable) throws ProductException;

    /**
     * Order a product with a given amount
     *
     * @param amount    the amount
     * @param productId the id of the product
     * @throws ProductException if amount is negative, if invalid product id is supplied
     *                          or if the amount is greater than the quantity
     */
    void orderProduct(int amount, int productId) throws ProductException;

    /**
     * @return list of categories and amount of products for each category
     */
    List<ProductsByCategory> getCategories();

}
