package com.lcwd.electronic.store.helper;

import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.dtos.UserDto;
import com.lcwd.electronic.store.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.List;

public class Helper {
    public static <U, V> PageableResponse<V> getPageableResponse(Page<U> page, Class<V> type) {
        List<U> entity = page.getContent();
//        return userList.stream().map(user -> entityToDto(user)).toList();
        List<V> dtoList = entity.stream().map(object -> new ModelMapper().map(object, type)).toList();
        return PageableResponse
                .<V>builder()
                .content(dtoList)
                .pageNumber(page.getNumber())
                .totalElements(page.getTotalElements())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .lastPage(page.isLast())
                .build();
    }
}
