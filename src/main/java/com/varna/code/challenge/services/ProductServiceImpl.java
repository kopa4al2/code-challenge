package com.varna.code.challenge.services;

import com.varna.code.challenge.exceptions.ProductException;
import com.varna.code.challenge.models.binding.EditProductBinding;
import com.varna.code.challenge.models.binding.PageableAndSortable;
import com.varna.code.challenge.models.binding.ProductBinding;
import com.varna.code.challenge.models.entities.Product;
import com.varna.code.challenge.models.view.ProductByPage;
import com.varna.code.challenge.models.view.ProductView;
import com.varna.code.challenge.models.view.ProductsByCategory;
import com.varna.code.challenge.repositories.ProductRepository;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService
{
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    @Override
    public ProductView addProduct(ProductBinding productToAdd) throws ProductException
    {
        // Check if we have the correct binding model
        if (productToAdd.getName() == null || productToAdd.getCategory() == null ||
                productToAdd.getName().isBlank() || productToAdd.getCategory().isBlank())
            throw new ProductException("Invalid product passed, you need name and category");

        // Check if we already have such product and if we do, increase quantity, else insert it with quantity = 1
        Product p = productRepository.findByName(productToAdd.getName());
        if (p != null) {
            // If we pass existing product name but with different category,
            // the new category will be ignored and only the previous product's quantity
            // will be increased
            p.setQuantity(p.getQuantity() + 1);
            p.setLastModifiedDate(LocalDate.now());
            Product insertedProduct = productRepository.save(p);
            return new ProductView(insertedProduct);
        }

        try {

            Product returnProduct = productRepository.save(new Product(productToAdd));
            return new ProductView(returnProduct);

        } catch (DataIntegrityViolationException invalidValues) {
            // Catch it here so in the controller we catch more generic ProductException and pass the user correct msg
            throw new ProductException("You inserted too long values, name and category cannot exceed 16 symbols");
        }
    }

    @Override
    public void deleteProduct(int productId) throws ProductException
    {
        if (!productRepository.existsById(productId))
            throw new ProductException("No such product");

        productRepository.deleteById(productId);
    }

    @Override
    public ProductView updateProduct(int productId, EditProductBinding productToEdit) throws ProductException
    {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new ProductException("There is no product with such id");
        } else {
            Product p = product.get();
            mapNotNullValues(p, productToEdit);
            p.setLastModifiedDate(LocalDate.now());

            try {
                productRepository.save(p);
            } catch (DataIntegrityViolationException invalidValues) {
                // Catch it here so in the controller we catch more generic ProductException
                throw new ProductException("You inserted too long values, name and category cannot exceed 16 symbols");
            }

            return new ProductView(p);
        }
    }

    @Override
    public List<ProductView> getAllByPage(int pageNumber, int itemsPerPage) throws ProductException
    {
        if (pageNumber < 0 || itemsPerPage <= 0)
            throw new ProductException("Page number and items per page should be positive");

        return productRepository.findAll(PageRequest.of(pageNumber, itemsPerPage))
                .map(ProductView::new)
                .stream().collect(Collectors.toList());
    }

    @Override
    public ProductByPage getProducts(int pageNumber,
                                     int pageSize,
                                     @Nullable String orderBy,
                                     @Nullable String orderDirection) throws IllegalArgumentException
    {
        if ((orderBy != null && orderDirection == null) ||
                (orderBy == null && orderDirection != null)) {
            throw new IllegalArgumentException("You must either provide both orderBy and direction, or none");
        } else if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Invalid page number or page size");
        } else if (orderBy == null) { // orderDirection is null if we are here
            // We only have pageNumber and pageSize
            long totalRecords = productRepository.count();
            List<ProductView> productsForPage = productRepository.findAll(PageRequest.of(pageNumber, pageSize))
                    .map(ProductView::new)
                    .stream().collect(Collectors.toList());

            return new ProductByPage(totalRecords, productsForPage);
        } else {
            // We have both pageNumber, pageSize and correct sort order and name
            try {
                Sort.Direction sortDirection = Sort.Direction.valueOf(orderDirection.toUpperCase());


                long totalRecords = productRepository.count();
                List<ProductView> productsForPage = productRepository.findAll(
                        PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, orderBy)))
                        .map(ProductView::new)
                        .stream().collect(Collectors.toList());

                return new ProductByPage(totalRecords, productsForPage);
            } catch (IllegalArgumentException wrongSortOrder) {
                // Rethrow the exception with my custom message
                throw new IllegalArgumentException("Sort order should be one of ASC or DESC (case insensitive)");
            } catch (PropertyReferenceException noSuchProperty) {
                throw new IllegalArgumentException("No such property to sort " + orderBy);
            }
        }
    }

    @Override
    public List<ProductView> getAllByPage(PageableAndSortable pageableAndSortable) throws ProductException
    {
        // map the sortedProperties hashmap to List<Sort.Order> which hibernate accepts
        List<Sort.Order> hibernateSortOrder = pageableAndSortable.getSortedProperties()
                .keySet().stream()
                .map(property ->
                        new Sort.Order(
                                Sort.Direction.fromString(
                                        pageableAndSortable.getSortedProperties().get(property).name()),
                                property))
                .collect(Collectors.toList());

        int pageNumber = pageableAndSortable.getPageNumber();
        int itemsPerPage = pageableAndSortable.getItemsPerPage();

        try {
            return productRepository.findAll(PageRequest.of(pageNumber, itemsPerPage, Sort.by(hibernateSortOrder)))
                    .map(ProductView::new)
                    .stream().collect(Collectors.toList());
        } catch (PropertyReferenceException e) {
            throw new ProductException("Wrong sort order, no property " + e.getPropertyName());
        }
    }

    @Override
    public long getProductCount()
    {
        return productRepository.count();
    }

    @Override
    public ProductView orderProduct(int amount, int productId) throws ProductException
    {
        // If i want to optimize i would check for negative amount first and then get
        // the product from database, but for me, the code looks cleaner this way
        Optional<Product> productToOrder = productRepository.findById(productId);

        if (amount <= 0) {
            throw new ProductException("Amount should be positive, greater than zero");
        } else if (productToOrder.isEmpty()) {
            throw new ProductException("No such product");
        } else if (productToOrder.get().getQuantity() < amount) {
            throw new ProductException("There isn't that much in stock");
        } else {
            Product p = productToOrder.get();
            p.setQuantity(p.getQuantity() - amount);

            return new ProductView(productRepository.save(p));
        }
    }

    @Override
    public List<ProductsByCategory> getCategories()
    {
        return productRepository.getProductsByCategory();
    }

    private void mapNotNullValues(Product p, EditProductBinding pe)
    {
        if (pe.getCategory() != null)
            p.setCategory(pe.getCategory());
        if (pe.getDescription() != null)
            p.setDescription(pe.getDescription());
        if (pe.getName() != null)
            p.setName(pe.getName());
    }
}
