package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.dtos.ProductDto;
import com.lcwd.electronic.store.services.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/products")
public class ProductController {
    Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    // CREATE
    @PostMapping("/create-product")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        logger.info("CREATING PRODUCT");
        ProductDto createdProduct = productService.create(productDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // UPDATE
    @PutMapping("/update-product/{productID}")
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto, @PathVariable int productID) {
        logger.info("UPDATING PRODUCT");
        ProductDto updatedProduct = productService.update(productDto, productID);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    // PARTIAL UPDATE(PATCH UPDATE)
    @PatchMapping("/patch-update-product/{productID}")
    public ResponseEntity<ProductDto> patchUpdateProduct(@RequestBody ProductDto productDto, @PathVariable int productID) {
        logger.info("PARTIALLY UPDATING PRODUCT");
        ProductDto patchedUpdate = productService.patchUpdate(productDto, productID);
        return new ResponseEntity<>(patchedUpdate, HttpStatus.OK);
    }

    // GET ALL
    @GetMapping("/getAllProducts")
    public ResponseEntity<PageableResponse<ProductDto>> getAllProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc", required = false) String sortDirection
    ) {
        PageableResponse<ProductDto> allProducts = productService.getAll(pageNumber, pageSize, sortBy, sortDirection);
        logger.info("GET ALL PRODUCTS");
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    // GET ALL LIVE
    @GetMapping("/getAllLiveProducts")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLiveProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc", required = false) String sortDirection
    ) {
        PageableResponse<ProductDto> allProducts = productService.getAllLiveStatus(pageNumber, pageSize, sortBy, sortDirection);
        logger.info("GET ALL LIVE PRODUCTS");
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    // GET SINGLE BY ID
    @GetMapping("/get-product-by-id/{productID}")
    public ResponseEntity<ProductDto> getSingleProduct(@PathVariable int productID) {
        logger.info("GET PRODUCT BY ID");
        ProductDto product = productService.getById(productID);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // GET SINGLE BY TITLE
    @GetMapping("/get-product-by-title/{title}")
    public ResponseEntity<ProductDto> getProductByTitle(@PathVariable String title) {
        logger.info("GET MULTIPLE PRODUCT BY ID");
        ProductDto singleProductByTitle = productService.getSingleProductByTitle(title);
        return new ResponseEntity<>(singleProductByTitle, HttpStatus.OK);
    }

    // DELETE SINGLE BY ID
    @DeleteMapping("/delete-product-by-id/{productID}")
    public ResponseEntity<ApiResponseMessage> deleteProductById(@PathVariable int productID) {
        logger.info("DELETING PRODUCT....");
        productService.delete(productID);
        logger.info("PRODUCT DELETED !!");
        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Product with ID " + productID + " is deleted successfully !!")
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    // DELETE MULTIPLE BY ID
    @DeleteMapping("/delete-multiple-products")
    public ResponseEntity<ApiResponseMessage> deleteMultipleProducts(@RequestParam Integer[] productIDs) {
        logger.info("DELETING MULTIPLE PRODUCTS....");
        productService.deleteMultiple(productIDs);
        logger.info("MULTIPLE PRODUCTS DELETED....");
        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .status(HttpStatus.OK)
                .success(true)
                .message("Product with IDs : " + Arrays.toString(productIDs) + " has been deleted successfully !!")
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    // DELETE ALL
    @DeleteMapping("/delete-products")
    public ResponseEntity<ApiResponseMessage> deleteAllProducts() {
        logger.info("DELETING ALL PRODUCTS....");
        productService.deleteAll();
        logger.info("ALL PRODUCTS DELETED....");
        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .status(HttpStatus.OK)
                .success(true)
                .message("ALL DATA DELETED SUCCESSFULLY !!")
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    // SEARCH PRODUCT
    @GetMapping("/search-products/{keywords}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProducts(
            @PathVariable(value = "keywords") String keywords,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc", required = false) String sortDirection
    ) {
        PageableResponse<ProductDto> searchedProduct = productService.search(keywords, pageNumber, pageSize, sortBy, sortDirection);
        return new ResponseEntity<>(searchedProduct, HttpStatus.OK);
    }
}