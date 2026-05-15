package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.CategoryDto;
import com.lcwd.electronic.store.dtos.PageableResponse;

import java.util.List;

public interface CategoryService {
    // CREATE
    CategoryDto createCategory(CategoryDto categoryDto);

    // UPDATE
    CategoryDto updateCategory(CategoryDto categoryDto, int categoryID);

    // UPDATE CATEGORY WITH PRODUCT
//    CategoryDto updateCategoryWithProduct(int productID, int categoryID);

    // PATCH UPDATE
    CategoryDto patchUpdate(CategoryDto categoryDto, int categoryID);

    // GET ALL
    PageableResponse<CategoryDto> getAllCategories(int pageNumber, int pageSize, String sortBy, String sortDirection);

    // GET SINGLE BY ID
    CategoryDto getSingleCategoryByID(int categoryID);

    // GET SINGLE BY Title
    CategoryDto getSingleCategoryByTitle(String title);

    // DELETE SINGLE
    void deleteCategory(int categoryID);

    // DELETE MULTIPLE CATEGORIES
    void deleteMultipleCategories(int[] categoryIDs);

    // DELETE ALL CATEGORIES
    void deleteAllCategories();

    // SEARCH CATEGORY
    List<CategoryDto> searchCategory(String keywords);
}
