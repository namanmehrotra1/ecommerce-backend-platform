package com.lcwd.electronic.store.services.implementations;

import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.dtos.ProductDto;
import com.lcwd.electronic.store.entities.Category;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.exceptions.BadRequestException;
import com.lcwd.electronic.store.exceptions.DuplicateResourceException;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.helper.Helper;
import com.lcwd.electronic.store.repositories.CategoryRepository;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.services.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDto create(ProductDto dto) {
        if (productRepository.existsByTitle(dto.getTitle())) {
            throw new DuplicateResourceException("Product with title " + dto.getTitle() + " already exists !!", HttpStatus.BAD_REQUEST);
        }
        Product productEntity = modelMapper.map(dto, Product.class);
//        productEntity.setAddedDate(new Date());
        Product savedProduct = productRepository.save(productEntity);
        logger.info("SAVED PRODUCT : {} ", savedProduct);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductDto update(ProductDto dto, Integer iD) {
        Product product = productRepository.findById(iD).orElseThrow(() -> new ResourceNotFoundException("Resource with Product ID : " + iD + " is not found", HttpStatus.NOT_FOUND));
        Product updatedProduct = Product.builder()
                .productID(product.getProductID())
                .title(dto.getTitle())
                .price(dto.getPrice())
                .discountedPrice(dto.getDiscountedPrice())
                .liveStatus(dto.getLiveStatus())
                .stock(dto.getStock())
                .addedDate(dto.getAddedDate())
                .quantity(dto.getQuantity())
                .description(dto.getDescription())
                .build();
        Product savedProduct = productRepository.save(updatedProduct);
        logger.info("UPDATED PRODUCT : {} ", savedProduct);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductDto patchUpdate(ProductDto dto, Integer iD) {
        Product product = productRepository.findById(iD).orElseThrow(() -> new ResourceNotFoundException("Resource with Product ID : " + iD + " is not found", HttpStatus.NOT_FOUND));
        if (dto.getTitle() != null) {
            String title = dto.getTitle().trim();
            if (title.length() < 10 || title.length() > 80) {
                throw new BadRequestException("Title must be between 10 and 80 characters !!", HttpStatus.BAD_REQUEST);
            }
        }
        if (dto.getDescription() != null) {
            String description = dto.getDescription().trim();
            if (description.length() < 10 || description.length() > 100) {
                throw new BadRequestException("Description must be between 10 and 100 characters !!", HttpStatus.BAD_REQUEST);
            }
        }
        if (dto.getQuantity() != null && dto.getQuantity() < 0) {
            throw new BadRequestException("Quantity can't be less than '0' !!", HttpStatus.BAD_REQUEST);
        }
        if (dto.getPrice() != null && dto.getPrice() < 0) {
            throw new BadRequestException("Price can't be negative !!", HttpStatus.BAD_REQUEST);
        }

        if (dto.getDiscountedPrice() != null && dto.getDiscountedPrice() < 0) {
            throw new BadRequestException("Discounted price can't be negative !!", HttpStatus.BAD_REQUEST);
        }
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.typeMap(ProductDto.class, Product.class).addMappings(mapper -> mapper.skip(Product::setProductID));
        modelMapper.map(dto, product);
        return modelMapper.map(productRepository.save(product), ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> allProducts = productRepository.findAll(pageable);
        logger.info("ALL FETCHED PRODUCTS : {} ", allProducts.getContent());
        return Helper.getPageableResponse(allProducts, ProductDto.class);

    }

    @Override
    public ProductDto getById(Integer iD) {
        Product product = productRepository.findById(iD).orElseThrow(() -> new ResourceNotFoundException("Resource with Product ID : " + iD + " is not found", HttpStatus.NOT_FOUND));
        logger.info("FETCHED PRODUCT BY ID : {} ", product);
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto getSingleProductByTitle(String title) {
        Product product = productRepository.findByTitle(title).orElseThrow(() -> new ResourceNotFoundException("Resource with Product title : " + title + " is not found", HttpStatus.NOT_FOUND));
        logger.info("FETCHED PRODUCT BY TITLE : {} ", product);
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAllLiveStatus(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort sort = (sortDirection.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> liveProducts = productRepository.findByLiveStatusTrue(pageable);
        logger.info("LIVE PRODUCTS LIST : {} ", liveProducts);
        return Helper.getPageableResponse(liveProducts, ProductDto.class);
    }

    @Override
    public ProductDto createProductWithCategory(ProductDto productDto, int categoryID) {
        // FETCH THE CATEGORY -> Use Category Repository
        Category category = categoryRepository.findById(categoryID).orElseThrow(() -> new ResourceNotFoundException("Category not found with ID : " + categoryID, HttpStatus.NOT_FOUND));
        if (productRepository.existsByTitle(productDto.getTitle())) {
            throw new DuplicateResourceException("Product with title " + productDto.getTitle() + " already exists !!", HttpStatus.BAD_REQUEST);
        }
        Product productEntity = modelMapper.map(productDto, Product.class);
        productEntity.setCategory(category);
        Product savedProduct = productRepository.save(productEntity);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductDto updateCategoryOfProduct(int categoryID, int productID) {
        Product product = productRepository.findById(productID).orElseThrow(() -> new ResourceNotFoundException("Resource with Product ID : " + productID + " is not found", HttpStatus.NOT_FOUND));
        Category category = categoryRepository.findById(categoryID).orElseThrow(() -> new ResourceNotFoundException("Resource with Category ID : " + categoryID + " is not found", HttpStatus.NOT_FOUND));
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAllProductsByCategory(int categoryID, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            throw new BadRequestException("Invalid Sort Direction : " + sortDirection + " . Allowed values are 'asc' or 'desc'.", HttpStatus.BAD_REQUEST);
        }
        Sort sort = (sortDirection.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Category category = categoryRepository.findById(categoryID).orElseThrow(() -> new ResourceNotFoundException("Resource with Category ID : " + categoryID + " is not found", HttpStatus.NOT_FOUND));
        Page<Product> byCategory = productRepository.findByCategory(category, pageable);
        return Helper.getPageableResponse(byCategory, ProductDto.class);
    }

    @Override
    public void delete(Integer iD) {
        Product product = productRepository.findById(iD).orElseThrow(() -> new ResourceNotFoundException("Resource with Product title : " + iD + " is not found", HttpStatus.NOT_FOUND));
        logger.info("DELETED PRODUCT : {} ", product);
        productRepository.delete(product);

    }

    @Override
    public void deleteMultiple(Integer[] ids) {
        List<Integer> productIDList = Arrays.stream(ids).toList();
        logger.info("DELETED MULTIPLE PRODUCT IDs : {} ", productIDList);
        productRepository.deleteAllById(productIDList);
    }

    @Override
    public void deleteAll() {
        productRepository.deleteAll();
        logger.info("ALL PRODUCTS DELETED");
    }

    @Override
    public PageableResponse<ProductDto> search(String keywords, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            throw new BadRequestException("Invalid Sort Direction : " + sortDirection + " . Allowed values are 'asc' or 'desc'.", HttpStatus.BAD_REQUEST);
        }
        Sort sort = (sortDirection.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> searchedProducts = productRepository.findByTitleContaining(keywords, pageable);
        logger.info("SEARCHED PRODUCTS LIST : {} ", searchedProducts);
        return Helper.getPageableResponse(searchedProducts, ProductDto.class);
    }

}
