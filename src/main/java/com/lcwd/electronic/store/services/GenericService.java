package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.PageableResponse;

public interface GenericService<T, ID> {
    // CREATE
    T create(T dto);

    // UPDATE
    T update(T dto, ID iD);

    // PATCH UPDATE
    T patchUpdate(T dto, ID iD);

    // GET ALL
    PageableResponse<T> getAll(int pageNumber, int pageSize, String sortBy, String sortDirection);

    // GET SINGLE BY ID
    T getById(ID iD);

    // DELETE SINGLE
    void delete(ID iD);

    // DELETE MULTIPLE CATEGORIES
    void deleteMultiple(ID[] ids);

    // DELETE ALL CATEGORIES
    void deleteAll();

    // SEARCH CATEGORY
    PageableResponse<T> search(String keywords, int pageNumber, int pageSize, String sortBy, String sortDirection);
}
