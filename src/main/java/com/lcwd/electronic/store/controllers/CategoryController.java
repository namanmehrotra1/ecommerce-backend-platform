package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.CategoryDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.dtos.ProductDto;
import com.lcwd.electronic.store.services.CategoryService;
import com.lcwd.electronic.store.services.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    // CREATE
    @PostMapping("/create-category")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto category = categoryService.createCategory(categoryDto);
        logger.info("CREATING CATEGORY");
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    // UPDATE
    @PutMapping("/update-category/{categoryID}")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto, @PathVariable int categoryID) {
        CategoryDto updateCategory = categoryService.updateCategory(categoryDto, categoryID);
        logger.info("UPDATING CATEGORY");
        return new ResponseEntity<>(updateCategory, HttpStatus.OK);
    }

    // PARTIAL UPDATE(PATCH UPDATE)
    @PatchMapping("/patch-update-category/{categoryID}")
    public ResponseEntity<CategoryDto> partialUpdateCategory(@RequestBody CategoryDto categoryDto, @PathVariable("categoryID") int categoryID) {
        CategoryDto patchedUpdate = categoryService.patchUpdate(categoryDto, categoryID);
        logger.info("PARTIALLY UPDATING CATEGORY");
        return new ResponseEntity<>(patchedUpdate, HttpStatus.OK);
    }

    // GET ALL
    @GetMapping("/getCategories")
    public ResponseEntity<PageableResponse<CategoryDto>> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "asc", required = false) String sortDirection) {
        PageableResponse<CategoryDto> allCategories = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortDirection);
        logger.info("FETCHING ALL CATEGORIES");
        return new ResponseEntity<>(allCategories, HttpStatus.OK);
    }

    // GET SINGLE BY ID
    @GetMapping("/get-category-by-id/{categoryID}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("categoryID") int categoryID) {
        logger.info("FETCHING CATEGORY BY ID.......");
        CategoryDto singleCategoryByID = categoryService.getSingleCategoryByID(categoryID);
        return new ResponseEntity<>(singleCategoryByID, HttpStatus.OK);
    }

    // GET SINGLE BY TITLE
    @GetMapping("/get-category-by-title/{title}")
    public ResponseEntity<CategoryDto> getCategoryByTitle(@PathVariable("title") String title) {
        logger.info("FETCHING CATEGORY BY TITLE.......");
        CategoryDto singleCategoryByTitle = categoryService.getSingleCategoryByTitle(title);
        return new ResponseEntity<>(singleCategoryByTitle, HttpStatus.OK);
    }

    // DELETE SINGLE BY ID
    @DeleteMapping("/delete-category-by-id/{categoryID}")
    public ResponseEntity<ApiResponseMessage> deleteCategoryByID(@PathVariable("categoryID") int categoryID) {
        logger.info("DELETING CATEGORIES.......");
        categoryService.deleteCategory(categoryID);
        ApiResponseMessage responseMessage = ApiResponseMessage
                .builder()
                .message("Category with ID : " + categoryID + " is deleted successfully")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    // DELETE MULTIPLE BY ID
    @DeleteMapping("/delete-multiple-categories")
    public ResponseEntity<ApiResponseMessage> deleteMultipleCategories(@RequestParam int[] categoryIDs) {
        logger.info("DELETING MULTIPLE CATEGORIES.......");
        categoryService.deleteMultipleCategories(categoryIDs);
        ApiResponseMessage response = ApiResponseMessage
                .builder()
                .message("Category with IDs : " + Arrays.toString(categoryIDs) + " are deleted successfully")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // DELETE ALL
    @DeleteMapping("/delete-categories")
    public ResponseEntity<ApiResponseMessage> deleteAllCategories() {
        logger.info("DELETING ALL CATEGORIES");
        categoryService.deleteAllCategories();
        ApiResponseMessage response = ApiResponseMessage
                .builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("ALL DATA DELETED SUCCESSFULLY")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // SEARCH CATEGORY
    @GetMapping("/search-category/{keywords}")
    public ResponseEntity<List<CategoryDto>> searchCategory(@PathVariable("keywords") String keywords) {
        logger.info("SEARCHING CATEGORIES......");
        List<CategoryDto> searchedCategory = categoryService.searchCategory(keywords);
        return new ResponseEntity<>(searchedCategory, HttpStatus.OK);
    }

    // CREATE PRODUCT WITH CATEGORY
    @PostMapping("/{categoryID}/create-product")
    public ResponseEntity<ProductDto> createProductWithCategory(@PathVariable int categoryID, @RequestBody ProductDto productDto) {
        ProductDto productWithCategory = productService.createProductWithCategory(productDto, categoryID);
        return new ResponseEntity<>(productWithCategory, HttpStatus.OK);
    }

    // UPDATE CATEGORY OF PRODUCT
    @PutMapping("/{categoryID}/products/{productID}")
    public ResponseEntity<ProductDto> updateCategoryOfProduct(@PathVariable int categoryID, @PathVariable int productID) {
        ProductDto updateCategoryOfProduct = productService.updateCategoryOfProduct(categoryID, productID);
        return new ResponseEntity<>(updateCategoryOfProduct, HttpStatus.OK);
    }

//    // UPDATE CATEGORY OF PRODUCT
//    @PutMapping("/add-product-in-category/{categoryID}/products/{productID}")
//    public ResponseEntity<CategoryDto> updateCategoryWithProduct(@PathVariable int categoryID, @PathVariable int productID) {
//        CategoryDto updateCategoryWithProduct = categoryService.updateCategoryWithProduct(productID, categoryID);
//        return new ResponseEntity<>(updateCategoryWithProduct, HttpStatus.OK);
//    }

    // GET PRODUCTS OF CATEGORIES
    @GetMapping("/getProductsOfCategories/{categoryID}")
    public ResponseEntity<PageableResponse<ProductDto>> getProductsOfCategories
    (@PathVariable int categoryID,
     @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
     @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize,
     @RequestParam(name = "sortBy", defaultValue = "title", required = false) String sortBy,
     @RequestParam(name = "sortDirection", defaultValue = "asc", required = false) String sortDirection) {
        PageableResponse<ProductDto> allProductsByCategory = productService.getAllProductsByCategory(categoryID, pageNumber, pageSize, sortBy, sortDirection);
        return new ResponseEntity<>(allProductsByCategory, HttpStatus.OK);
    }
}
