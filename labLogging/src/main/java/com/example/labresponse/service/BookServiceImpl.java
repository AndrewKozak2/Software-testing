package com.example.labresponse.service;

import com.example.labresponse.model.Book;
import com.example.labresponse.repo.BookRepository;
import com.example.labresponse.request.PagingRequest;
import com.example.labresponse.response.ApiResponse;
import com.example.labresponse.response.BaseMetadata;
import com.example.labresponse.response.PagingMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book getById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public Book create(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book update(Long id, Book book) {
        if (bookRepository.existsById(id)) {
            book.setId(id);
            return bookRepository.save(book);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public ApiResponse<BaseMetadata, Book> getAllAsApiResponse() {
        List<Book> books = getAll();
        if (books.isEmpty()) {
            return new ApiResponse<>(BaseMetadata.builder()
                    .code(404)
                    .success(false)
                    .errorMessage("List is empty")
                    .build());
        }
        return new ApiResponse<>(new BaseMetadata(), books);
    }

    @Override
    public ApiResponse<BaseMetadata, Book> getByIdAsApiResponse(Long id) {
        Book book = getById(id);
        if (book == null) {
            return new ApiResponse<>(BaseMetadata.builder()
                    .code(404)
                    .success(false)
                    .errorMessage("Not found")
                    .build());
        }
        return new ApiResponse<>(new BaseMetadata(), book);
    }

    @Override
    public ApiResponse<BaseMetadata, Book> createAsApiResponse(Book book) {
        Book savedBook = create(book);
        return new ApiResponse<>(new BaseMetadata(), savedBook);
    }

    @Override
    public ApiResponse<BaseMetadata, Book> updateAsApiResponse(Long id, Book book) {
        Book updatedBook = update(id, book);
        if (updatedBook == null) {
            return new ApiResponse<>(BaseMetadata.builder()
                    .code(404)
                    .success(false)
                    .errorMessage("Not found")
                    .build());
        }
        return new ApiResponse<>(new BaseMetadata(), updatedBook);
    }

    @Override
    public ApiResponse<BaseMetadata, Book> deleteAsApiResponse(Long id) {
        if (!bookRepository.existsById(id)) {
            return new ApiResponse<>(BaseMetadata.builder()
                    .code(404)
                    .success(false)
                    .errorMessage("Not found")
                    .build());
        }
        delete(id);
        return new ApiResponse<>(new BaseMetadata());
    }

    @Override
    public ApiResponse<BaseMetadata, Book> getByAuthorAsApiResponse(String author) {
        List<Book> books = bookRepository.findByAuthor(author);
        if (books.isEmpty()) {
            return new ApiResponse<>(BaseMetadata.builder()
                    .code(404)
                    .success(false)
                    .errorMessage("No books by this author")
                    .build());
        }
        return new ApiResponse<>(new BaseMetadata(), books);
    }

    @Override
    public ApiResponse<PagingMetadata, Book> getPagedBooks(PagingRequest request) {
        if (request.getPage() < 0 || request.getSize() <= 0) {
            return new ApiResponse<>(PagingMetadata.builder()
                    .code(400)
                    .success(false)
                    .errorMessage("Invalid page or size")
                    .build());
        }

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Book> bookPage = bookRepository.findAll(pageable);

        if (bookPage.isEmpty()) {
            return new ApiResponse<>(PagingMetadata.builder()
                    .code(404)
                    .success(false)
                    .errorMessage("No books found for this page")
                    .currentPage(request.getPage())
                    .pageSize(request.getSize())
                    .totalElements(bookPage.getTotalElements())
                    .totalPages(bookPage.getTotalPages())
                    .build());
        }

        PagingMetadata metadata = PagingMetadata.builder()
                .code(200)
                .success(true)
                .currentPage(bookPage.getNumber())
                .pageSize(bookPage.getSize())
                .totalElements(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .build();

        return new ApiResponse<>(metadata, bookPage.getContent());
    }
}
