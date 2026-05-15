package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.dtos.ProductDto;

public interface ProductService extends GenericService<ProductDto, Integer> {
    // GET SINGLE BY TITLE
    ProductDto getSingleProductByTitle(String title);

    PageableResponse<ProductDto> getAllLiveStatus(int pageNumber, int pageSize, String sortBy, String sortDirection);

    // Create Product with Category
    ProductDto createProductWithCategory(ProductDto productDto, int categoryID);

    // Update Category of Product
    ProductDto updateCategoryOfProduct(int categoryID, int productID);

    PageableResponse<ProductDto> getAllProductsByCategory(int categoryID, int pageNumber, int pageSize, String sortBy, String sortDirection);
}
