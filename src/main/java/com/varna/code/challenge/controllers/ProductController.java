package com.varna.code.challenge.controllers;

import com.varna.code.challenge.exceptions.ProductException;
import com.varna.code.challenge.models.binding.EditProductBinding;
import com.varna.code.challenge.models.binding.PageableAndSortable;
import com.varna.code.challenge.models.binding.ProductBinding;
import com.varna.code.challenge.models.response.AbstractResponse;
import com.varna.code.challenge.models.response.FailedResponse;
import com.varna.code.challenge.models.response.SuccessResponse;
import com.varna.code.challenge.models.view.ProductView;
import com.varna.code.challenge.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService)
    {
        this.productService = productService;
    }

    /**
     * Create new product
     *
     * @param productBindingModel model with fields needed for new product insertion
     * @return the inserted product, or appropriate error
     */
    @PostMapping("/products/new")
    public ResponseEntity<AbstractResponse> addNewProduct(@RequestBody ProductBinding productBindingModel)
    {
        try {
            ProductView viewModel = productService.addProduct(productBindingModel);
            return ResponseEntity.ok(new SuccessResponse(viewModel));
        } catch (ProductException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new FailedResponse(e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected server error while adding product message: {} stack trace: {}",
                    e.getMessage(),
                    e.getStackTrace());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FailedResponse("Unexpected error occurred"));
        }
    }

    /**
     * Delete a product by id
     *
     * @param productId the id of the product to delete
     * @return response with appropriate status and message
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AbstractResponse> deleteProduct(@PathVariable(name = "id") int productId)
    {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok(new SuccessResponse("Successfully deleted the product"));
        } catch (ProductException e) {
            return ResponseEntity.badRequest().body(new FailedResponse(e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected server error while deleting product message: {} stack trace: {}",
                    e.getMessage(),
                    e.getStackTrace());
            return ResponseEntity.badRequest().body(new FailedResponse("Unexpected error"));
        }
    }

    /**
     * Perform update action on an object with given id
     * will ignore null fields
     *
     * @param productId        the id of the product to update
     * @param editProductModel the fields to be updated (nulls are ignored, empty strings are not)
     * @return the updated product or appropriate response
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<AbstractResponse> updateProduct(@PathVariable(name = "id") int productId,
                                                          @RequestBody EditProductBinding editProductModel)

    {
        try {
            return ResponseEntity.ok(new SuccessResponse(productService.updateProduct(productId, editProductModel)));
        } catch (ProductException e) {
            return ResponseEntity.badRequest().body(new FailedResponse(e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected server error while updating product message: {} stack trace: {}",
                    e.getMessage(),
                    e.getStackTrace());
            return ResponseEntity.badRequest().body(new FailedResponse("Unexpected error"));
        }
    }


    /**
     * @return all categories with product count per category
     */
    @GetMapping("/categories")
    public ResponseEntity<AbstractResponse> getAllCategories()
    {
        try {
            return ResponseEntity.ok(new SuccessResponse(productService.getCategories()));
        } catch (Exception e) {
            LOGGER.error("Unexpected server error while listing categories message: {} stack trace: {}",
                    e.getMessage(),
                    e.getStackTrace());
            return ResponseEntity.badRequest().body(new FailedResponse("Unexpected error"));
        }
    }

    /**
     * I later realized i was supposed to create a rest api with the exact end points and responses
     * so this is the implementation of paging and sorting which is not used in my front end
     * but can be accessed by writing the query in the browser url bar
     *
     * @param page      the page to show
     * @param pageSize  the page size
     * @param orderBy   the property to order
     * @param direction the direction to order
     * @return response with products paged and sorted by single field
     */
    @GetMapping("/products")
    public ResponseEntity<AbstractResponse> getAllProducts(@RequestParam int page,
                                                           @RequestParam int pageSize,
                                                           @RequestParam(required = false) String orderBy,
                                                           @RequestParam(required = false) String direction)
    {
        try {
            return ResponseEntity.ok(
                    new SuccessResponse(
                            productService.getProducts(page, pageSize, orderBy, direction)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new FailedResponse(e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected server error when getting product message: {} stack trace: {}",
                    e.getMessage(),
                    e.getStackTrace());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new FailedResponse("Unexpected error occurred"));
        }
    }

    /**
     * @return count of all products in the table
     */
    @GetMapping("/products/count")
    public ResponseEntity<AbstractResponse> getProductsCount()
    {
        try {
            return ResponseEntity.ok(
                    new SuccessResponse(
                            productService.getProductCount()));
        } catch (Exception e) {
            LOGGER.error("Unexpected server error when getting product count message: {} stack trace: {}",
                    e.getMessage(),
                    e.getStackTrace());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new FailedResponse("Unexpected error occurred"));
        }
    }

    /**
     * Get all products by page
     *
     * @param offset the page number
     * @param limit  the numbers per page
     * @return the products for desired page or appropriate error message
     */
    @GetMapping("/products/all")
    public ResponseEntity<AbstractResponse> getAllByPage(@RequestParam int offset, @RequestParam int limit)
    {
        try {
            return ResponseEntity.ok(
                    new SuccessResponse(
                            productService.getAllByPage(offset, limit)));
        } catch (ProductException e) {
            return ResponseEntity.badRequest().body(
                    new FailedResponse(e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected server error when getting product message: {} stack trace: {}",
                    e.getMessage(),
                    e.getStackTrace());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new FailedResponse("Unexpected error occurred"));
        }
    }

    /**
     * Get all products, by page with sort
     * I use post request for this, because i do not know the number of query parameters
     * and i consume an object with the necessary sort fields, and page size/items
     *
     * @param pageableAndSortable the model with sort data and page number / size
     * @return response with appropriate status (success or failure) and
     * if success, response body with paged and sorted products
     */
    @PostMapping("/products/all")
    public ResponseEntity<AbstractResponse> getAllByPageWithSort(@RequestBody PageableAndSortable pageableAndSortable)
    {
        try {
            return ResponseEntity.ok(
                    new SuccessResponse(
                            productService.getAllByPage(pageableAndSortable)));
        } catch (ProductException e) {
            return ResponseEntity.badRequest().body(
                    new FailedResponse(e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected server error when getting product message: {} stack trace: {}",
                    e.getMessage(),
                    e.getStackTrace());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new FailedResponse("Unexpected error occurred"));
        }
    }

    /**
     * Order a product.
     *
     * @param id     the id of the product to order
     * @param amount the amount to order
     * @return response with appropriate message
     */
    @PostMapping("/product/{id}/order/{amount}")
    public ResponseEntity<AbstractResponse> orderProducts(@PathVariable int id, @PathVariable int amount)
    {
        try {
            productService.orderProduct(amount, id);
            return ResponseEntity.ok(
                    new SuccessResponse("Successfully Ordered product"));
        } catch (ProductException e) {
            return ResponseEntity.badRequest().body(
                    new FailedResponse(e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected server error when ordering product with id: {}, amount: {}," +
                            " message: {} stack trace: {}",
                    id,
                    amount,
                    e.getMessage(),
                    e.getStackTrace());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new FailedResponse("Unexpected error occurred"));
        }
    }
}
