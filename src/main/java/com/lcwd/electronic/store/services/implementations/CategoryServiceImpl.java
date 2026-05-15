package com.lcwd.electronic.store.services.implementations;

import com.lcwd.electronic.store.dtos.CategoryDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.entities.Category;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.exceptions.BadRequestException;
import com.lcwd.electronic.store.exceptions.DuplicateResourceException;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.helper.Helper;
import com.lcwd.electronic.store.repositories.CategoryRepository;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.services.CategoryService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByTitle(categoryDto.getTitle())) {
            throw new DuplicateResourceException("Category with title '" + categoryDto.getTitle() + "' already exists", HttpStatus.BAD_REQUEST);
        }
        Category categoryEntity = modelMapper.map(categoryDto, Category.class);
        Category savedCategory = categoryRepository.save(categoryEntity);
        logger.info("SAVED CATEGORY {} ", savedCategory);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, int categoryID) {
        Category categoryEntity = categoryRepository.findById(categoryID).orElseThrow(() -> new ResourceNotFoundException("Resource with Category ID : " + categoryID + " is not found", HttpStatus.NOT_FOUND));
        Category updatedCategory = Category.builder()
                .iD(categoryEntity.getID())
                .title(categoryDto.getTitle())
                .description(categoryDto.getDescription())
                .coverImage(categoryDto.getCoverImage())
                .build();
        Category savedCategory = categoryRepository.save(updatedCategory);
        logger.info("UPDATED CATEGORY : {} ", savedCategory);
        return (modelMapper.map(savedCategory, CategoryDto.class));
    }

//    @Override
//    public CategoryDto updateCategoryWithProduct(int productID, int categoryID) {
//        Category category = categoryRepository.findById(categoryID).orElseThrow(() -> new ResourceNotFoundException("Resource with Category ID : " + categoryID + " is not found", HttpStatus.NOT_FOUND));
//        Product product = productRepository.findById(productID).orElseThrow(() -> new ResourceNotFoundException("Resource with Product ID : " + productID + " is not found", HttpStatus.NOT_FOUND));
//        product.setCategory(category);
//        category.getProducts().add(product);
//        Category savedCategory = categoryRepository.save(category);
//        return modelMapper.map(savedCategory, CategoryDto.class);
//    }

    @Override
    public CategoryDto patchUpdate(CategoryDto categoryDto, int categoryID) {
        Category categoryEntity = categoryRepository.findById(categoryID).orElseThrow(() -> new ResourceNotFoundException("Resource with Category ID : " + categoryID + " is not found", HttpStatus.NOT_FOUND));
//        Optional.ofNullable(categoryDto.getTitle()).ifPresent(categoryEntity::setTitle);
//        Optional.ofNullable(categoryDto.getDescription()).ifPresent(categoryEntity::setDescription);
//        Optional.ofNullable(categoryDto.getCoverImage()).ifPresent(categoryEntity::setCoverImage);
        if (categoryDto.getTitle() != null) {
            String title = categoryDto.getTitle().trim();
            if (title.length() < 4 || title.length() > 40) {
                throw new BadRequestException("Title must be between 4 and 40 characters !!", HttpStatus.BAD_REQUEST);
            }
        }
        if (categoryDto.getDescription() != null) {
            String description = categoryDto.getDescription().trim();
            if (description.length() < 10 || description.length() > 100) {
                throw new BadRequestException("Description must be between 10 and 100 characters !!", HttpStatus.BAD_REQUEST);
            }
        }
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.typeMap(CategoryDto.class, Category.class).addMappings(mapper -> mapper.skip(Category::setID));
        modelMapper.map(categoryDto, categoryEntity);
        logger.info("PARTIALLY UPDATED USER : {} ", categoryEntity);
        return modelMapper.map(categoryRepository.save(categoryEntity), CategoryDto.class);
    }

    @Override
    public PageableResponse<CategoryDto> getAllCategories(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            throw new BadRequestException("Invalid Sort Direction : " + sortDirection + " . Allowed values are 'asc' or 'desc'.", HttpStatus.BAD_REQUEST);
        }
        Sort sort = (sortDirection.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> page = categoryRepository.findAll(pageable);
        logger.info("ALL FETCHED CATEGORIES {} ", page.getContent());
        return Helper.getPageableResponse(page, CategoryDto.class);
    }

    @Override
    public CategoryDto getSingleCategoryByID(int categoryID) {
        Category category = categoryRepository.findById(categoryID).orElseThrow(() -> new ResourceNotFoundException("Resource with Category ID : " + categoryID + " is not found", HttpStatus.NOT_FOUND));
        logger.info("FETCHED CATEGORY BY ID : {} ", category);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public CategoryDto getSingleCategoryByTitle(String title) {
        Category category = categoryRepository.findByTitle(title).orElseThrow(() -> new ResourceNotFoundException("Resource with Category Title : " + title + " is not found", HttpStatus.NOT_FOUND));
        logger.info("FETCHED CATEGORY BY TITLE : {} ", category);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public void deleteCategory(int categoryID) {
        Category category = categoryRepository.findById(categoryID).orElseThrow(() -> new ResourceNotFoundException("Resource with Category Title : " + categoryID + " is not found", HttpStatus.NOT_FOUND));
        logger.info("DELETED CATEGORY : {} ", category);
        categoryRepository.delete(category);
    }

    @Override
    public void deleteMultipleCategories(int[] categoryIDs) {
        List<Integer> categoryIDList = Arrays.stream(categoryIDs).boxed().toList();
        categoryRepository.deleteAllById(categoryIDList);
        logger.info("DELETED MULTIPLE CATEGORY IDs : {} ", categoryIDList);
    }

    @Override
    public void deleteAllCategories() {
        categoryRepository.deleteAll();
        logger.info("ALL CATEGORIES DELETED");
    }

    @Override
    public List<CategoryDto> searchCategory(String keywords) {
        List<Category> byTitleContaining = categoryRepository.findByTitleContaining(keywords);
        List<CategoryDto> categoryDtoList = byTitleContaining.stream().map(category -> modelMapper.map(category, CategoryDto.class)).toList();
        logger.info("SEARCHED CATEGORIES LIST : {} ", categoryDtoList);
        return categoryDtoList;
    }
}
