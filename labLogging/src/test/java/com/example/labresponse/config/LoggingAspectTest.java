package com.example.labresponse.config;

import com.example.labresponse.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
public class LoggingAspectTest {

    @Autowired
    private BookService bookService;

    @Test
    void testLoggingAspect(CapturedOutput output) {
        bookService.getAll();

        assertThat(output.getOut()).contains("Entering method: getAll");
        assertThat(output.getOut()).contains("Exiting method: getAll");
    }

    @Test
    void testLoggingGetByIdWithArguments(CapturedOutput output) {
        bookService.getById(1L);

        assertThat(output.getOut()).contains("Entering method: getById with arguments: [1]");
        assertThat(output.getOut()).contains("Exiting method: getById");
    }

    @Test
    void testLoggingCreateWithReturnValue(CapturedOutput output) {
        com.example.labresponse.model.Book book = new com.example.labresponse.model.Book("Test", "Author", "Desc");
        bookService.create(book);

        assertThat(output.getOut()).contains("Entering method: create");
        assertThat(output.getOut()).contains("Exiting method: create with result:");
        assertThat(output.getOut()).contains("Test");
    }

    @Test
    void testLoggingDeleteMethod(CapturedOutput output) {
        bookService.delete(1L);

        assertThat(output.getOut()).contains("Entering method: delete with arguments: [1]");
        assertThat(output.getOut()).contains("Exiting method: delete");
    }

    @Test
    void testLoggingGetByAuthor(CapturedOutput output) {
        bookService.getByAuthorAsApiResponse("Joshua Bloch");

        assertThat(output.getOut()).contains("Entering method: getByAuthorAsApiResponse with arguments: [Joshua Bloch]");
        assertThat(output.getOut()).contains("Exiting method: getByAuthorAsApiResponse");
    }

    @Test
    void testLoggingUpdateWithTwoArguments(CapturedOutput output) {
        com.example.labresponse.model.Book book = new com.example.labresponse.model.Book("Updated", "Author", "Desc");
        bookService.update(1L, book);

        assertThat(output.getOut()).contains("Entering method: update with arguments: [1, Book");
        assertThat(output.getOut()).contains("Updated");
        assertThat(output.getOut()).contains("Exiting method: update");
    }

    @Test
    void testLoggingPagedBooksWithRequestObject(CapturedOutput output) {
        com.example.labresponse.request.PagingRequest request = new com.example.labresponse.request.PagingRequest(0, 5);
        bookService.getPagedBooks(request);

        assertThat(output.getOut()).contains("Entering method: getPagedBooks with arguments: [PagingRequest(page=0, size=5)]");
        assertThat(output.getOut()).contains("Exiting method: getPagedBooks");
    }

    @Test
    void testLoggingGetAllAsApiResponse(CapturedOutput output) {
        bookService.getAllAsApiResponse();

        assertThat(output.getOut()).contains("Entering method: getAllAsApiResponse with arguments: []");
        assertThat(output.getOut()).contains("Exiting method: getAllAsApiResponse");
    }

    @Test
    void testLoggingUpdateAsApiResponse(CapturedOutput output) {
        com.example.labresponse.model.Book book = new com.example.labresponse.model.Book("Title", "Author", "Desc");
        bookService.updateAsApiResponse(1L, book);

        assertThat(output.getOut()).contains("Entering method: updateAsApiResponse with arguments: [1, Book");
        assertThat(output.getOut()).contains("Exiting method: updateAsApiResponse");
    }

    @Test
    void testLoggingGetByAuthorFailure(CapturedOutput output) {
        bookService.getByAuthorAsApiResponse("NonExistentAuthor");

        assertThat(output.getOut()).contains("Entering method: getByAuthorAsApiResponse with arguments: [NonExistentAuthor]");
        assertThat(output.getOut()).contains("Exiting method: getByAuthorAsApiResponse with result:");
        assertThat(output.getOut()).contains("success=false");
    }
}
