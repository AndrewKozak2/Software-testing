package com.example.labresponse.service;

import com.example.labresponse.model.Book;
import com.example.labresponse.request.PagingRequest;
import com.example.labresponse.response.ApiResponse;
import com.example.labresponse.response.BaseMetadata;
import com.example.labresponse.response.PagingMetadata;

import java.util.List;

public interface BookService {
    List<Book> getAll();
    Book getById(Long id);
    Book create(Book book);
    Book update(Long id, Book book);
    void delete(Long id);

    ApiResponse<BaseMetadata, Book> getAllAsApiResponse();
    ApiResponse<BaseMetadata, Book> getByIdAsApiResponse(Long id);
    ApiResponse<BaseMetadata, Book> createAsApiResponse(Book book);
    ApiResponse<BaseMetadata, Book> updateAsApiResponse(Long id, Book book);
    ApiResponse<BaseMetadata, Book> deleteAsApiResponse(Long id);
    ApiResponse<BaseMetadata, Book> getByAuthorAsApiResponse(String author);

    ApiResponse<PagingMetadata, Book> getPagedBooks(PagingRequest request);
}
