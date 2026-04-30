package com.example.labresponse;

import com.example.labresponse.model.Book;
import com.example.labresponse.repo.BookRepository;
import com.example.labresponse.request.PagingRequest;
import com.example.labresponse.response.ApiResponse;
import com.example.labresponse.response.BaseMetadata;
import com.example.labresponse.response.PagingMetadata;
import com.example.labresponse.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService underTest;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    private void seedBooks(int count) {
        List<Book> books = IntStream.range(0, count)
                .mapToObj(i -> new Book("Title " + i, "Author " + i, "Desc " + i))
                .toList();
        bookRepository.saveAll(books);
    }

    @Test
    @DisplayName("1. Пошук за автором: Знайти всі книги певного автора")
    void whenSearchByAuthor_thenReturnFilteredList() {
        // Given
        bookRepository.save(new Book("Title 1", "Author A", "Desc 1"));
        bookRepository.save(new Book("Title 2", "Author A", "Desc 2"));
        bookRepository.save(new Book("Title 3", "Author B", "Desc 3"));

        // When
        ApiResponse<BaseMetadata, Book> response = underTest.getByAuthorAsApiResponse("Author A");

        // Then
        assertEquals(200, response.getMeta().getCode());
        assertEquals(2, response.getData().size(), "Should find 2 books by Author A");
    }

    @Test
    @DisplayName("13. Пагінація: Порожня база - повертає 404")
    void whenDbIsEmpty_getPaged_returns404() {
        PagingRequest request = new PagingRequest(0, 10);
        ApiResponse<PagingMetadata, Book> response = underTest.getPagedBooks(request);

        assertEquals(404, response.getMeta().getCode());
        assertEquals("No books found for this page", response.getMeta().getErrorMessage());
    }

    @Test
    @DisplayName("14. Пагінація: Перша сторінка (Page 0, Size 2)")
    void whenValidRequest_getPaged_returnsFirstPage() {
        seedBooks(5);
        PagingRequest request = new PagingRequest(0, 2);
        ApiResponse<PagingMetadata, Book> response = underTest.getPagedBooks(request);

        assertEquals(200, response.getMeta().getCode());
        assertEquals(2, response.getData().size());
        assertEquals(0, response.getMeta().getCurrentPage());
        assertEquals(5, response.getMeta().getTotalElements());
        assertEquals(3, response.getMeta().getTotalPages());
    }

    @Test
    @DisplayName("15. Пагінація: Друга сторінка (Page 1, Size 2)")
    void whenValidRequest_getPaged_returnsSecondPage() {
        seedBooks(5);
        PagingRequest request = new PagingRequest(1, 2);
        ApiResponse<PagingMetadata, Book> response = underTest.getPagedBooks(request);

        assertEquals(200, response.getMeta().getCode());
        assertEquals(2, response.getData().size());
        assertEquals(1, response.getMeta().getCurrentPage());
        assertEquals("Title 2", response.getData().get(0).getTitle());
    }

    @Test
    @DisplayName("16. Пагінація: Остання (часткова) сторінка")
    void whenValidRequest_getPaged_returnsLastPartialPage() {
        seedBooks(5);
        PagingRequest request = new PagingRequest(2, 2);
        ApiResponse<PagingMetadata, Book> response = underTest.getPagedBooks(request);

        assertEquals(200, response.getMeta().getCode());
        assertEquals(1, response.getData().size());
        assertEquals(2, response.getMeta().getCurrentPage());
    }

    @Test
    @DisplayName("17. Пагінація: Некоректний номер сторінки (від'ємний)")
    void whenNegativePage_returns400() {
        PagingRequest request = new PagingRequest(-1, 10);
        ApiResponse<PagingMetadata, Book> response = underTest.getPagedBooks(request);

        assertEquals(400, response.getMeta().getCode());
        assertEquals("Invalid page or size", response.getMeta().getErrorMessage());
    }

    @Test
    @DisplayName("18. Пагінація: Некоректний розмір сторінки (0)")
    void whenZeroSize_returns400() {
        PagingRequest request = new PagingRequest(0, 0);
        ApiResponse<PagingMetadata, Book> response = underTest.getPagedBooks(request);

        assertEquals(400, response.getMeta().getCode());
    }

    @Test
    @DisplayName("19. Пагінація: Сторінка поза межами (Page 10)")
    void whenPageOutOfBounds_returns404() {
        seedBooks(5);
        PagingRequest request = new PagingRequest(10, 2);
        ApiResponse<PagingMetadata, Book> response = underTest.getPagedBooks(request);

        assertEquals(404, response.getMeta().getCode());
    }

    @Test
    @DisplayName("20. Пагінація: Розмір більше за кількість елементів")
    void whenLargeSize_returnsAllInOnePage() {
        seedBooks(5);
        PagingRequest request = new PagingRequest(0, 100);
        ApiResponse<PagingMetadata, Book> response = underTest.getPagedBooks(request);

        assertEquals(200, response.getMeta().getCode());
        assertEquals(5, response.getData().size());
        assertEquals(1, response.getMeta().getTotalPages());
    }

    @Test
    @DisplayName("21. Пагінація: Перевірка метаданих (Total Elements)")
    void verifyMetadata_totalElements_isCorrect() {
        seedBooks(15);
        PagingRequest request = new PagingRequest(0, 5);
        ApiResponse<PagingMetadata, Book> response = underTest.getPagedBooks(request);

        assertEquals(15, response.getMeta().getTotalElements());
    }

    @Test
    @DisplayName("22. Пагінація: Перевірка метаданих (Total Pages)")
    void verifyMetadata_totalPages_isCorrect() {
        seedBooks(15);
        PagingRequest request = new PagingRequest(0, 4);
        ApiResponse<PagingMetadata, Book> response = underTest.getPagedBooks(request);

        // 15 elements, size 4 -> 4 pages (4, 4, 4, 3)
        assertEquals(4, response.getMeta().getTotalPages());
    }

    @Test
    @DisplayName("2. Пошук за ID: Повернення OK ApiResponse")
    void whenBookExistsThenReturnAsOkApiResponse() {
        // Given
        Book savedBook = bookRepository.save(new Book("Clean Code", "Robert Martin", "Practical handbook"));
        Long id = savedBook.getId();

        // When
        ApiResponse<BaseMetadata, Book> response = underTest.getByIdAsApiResponse(id);

        // Then
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
        assertEquals(id, response.getData().get(0).getId());
        assertEquals("Clean Code", response.getData().get(0).getTitle());
    }

    @Test
    @DisplayName("3. Пошук за ID: Повернення 404 (Not found)")
    void whenBookNotExistsThenReturn404ApiResponse() {
        // When
        ApiResponse<BaseMetadata, Book> response = underTest.getByIdAsApiResponse(999L);

        // Then
        assertEquals(404, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
        assertEquals("Not found", response.getMeta().getErrorMessage());
    }

    @Test
    @DisplayName("4. Отримати всі: Список заповнений")
    void whenBooksInDb_getAll_returnsList() {
        // Given
        bookRepository.save(new Book("Test Book", "Test Author", "Test Desc"));

        // When
        ApiResponse<BaseMetadata, Book> response = underTest.getAllAsApiResponse();

        // Then
        assertTrue(response.getMeta().isSuccess());
        assertFalse(response.getData().isEmpty());
    }

    @Test
    @DisplayName("5. Отримати всі: Список порожній (404)")
    void whenDbIsEmpty_getAll_returnsError() {
        // When
        ApiResponse<BaseMetadata, Book> response = underTest.getAllAsApiResponse();

        // Then
        assertEquals(404, response.getMeta().getCode());
        assertEquals("List is empty", response.getMeta().getErrorMessage());
    }

    @Test
    @DisplayName("6. Створення: Успішне збереження")
    void whenCreateBook_persistsInDb() {
        // Given
        Book newBook = new Book("Effective Java", "Joshua Bloch", "Best practices");

        // When
        ApiResponse<BaseMetadata, Book> response = underTest.createAsApiResponse(newBook);
        Book savedItem = response.getData().get(0);

        // Then
        assertNotNull(savedItem.getId());
        assertTrue(bookRepository.existsById(savedItem.getId()));
    }

    @Test
    @DisplayName("7. Оновлення: Успішна зміна даних")
    void whenUpdateBook_changesReflected() {
        // Given
        Book book = bookRepository.save(new Book("Old Title", "Author", "Old Desc"));
        book.setTitle("New Title");

        // When
        ApiResponse<BaseMetadata, Book> response = underTest.updateAsApiResponse(book.getId(), book);

        // Then
        assertEquals(200, response.getMeta().getCode());
        assertEquals("New Title", response.getData().get(0).getTitle());
    }

    @Test
    @DisplayName("9. Оновлення: Об'єкта не існує (404)")
    void whenUpdateNonExistentBook_returns404() {
        // Given
        Book book = new Book("Fake", "Author", "No desc");

        // When
        ApiResponse<BaseMetadata, Book> response = underTest.updateAsApiResponse(999L, book);

        // Then
        assertEquals(404, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
    }

    @Test
    @DisplayName("8. Видалення: Успішне видалення")
    void whenDeleteBook_removedFromDb() {
        // Given
        Book book = bookRepository.save(new Book("To be deleted", "Author", "Desc"));
        Long id = book.getId();

        // When
        ApiResponse<BaseMetadata, Book> response = underTest.deleteAsApiResponse(id);

        // Then
        assertTrue(response.getMeta().isSuccess());
        assertFalse(bookRepository.existsById(id));
    }

    @Test
    @DisplayName("10. Видалення: Об'єкта не існує (404)")
    void whenDeleteNonExistentBook_returns404() {
        // When
        ApiResponse<BaseMetadata, Book> response = underTest.deleteAsApiResponse(999L);

        // Then
        assertEquals(404, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
    }

    @Test
    @DisplayName("11. Пошук за автором: Книг автора не знайдено (404)")
    void whenAuthorHasNoBooks_returns404() {
        // When
        ApiResponse<BaseMetadata, Book> response = underTest.getByAuthorAsApiResponse("Unknown Author");

        // Then
        assertEquals(404, response.getMeta().getCode());
        assertEquals("No books by this author", response.getMeta().getErrorMessage());
    }

    @Test
    @DisplayName("12. Створення: Перевірка цілісності даних")
    void whenCreateBook_allFieldsAreCorrect() {
        // Given
        Book bookToSave = Book.builder()
                .title("Advanced Java")
                .author("Expert")
                .description("Deep dive")
                .build();

        // When
        ApiResponse<BaseMetadata, Book> response = underTest.createAsApiResponse(bookToSave);
        Book saved = response.getData().get(0);

        // Then
        assertEquals("Advanced Java", saved.getTitle());
        assertEquals("Expert", saved.getAuthor());
        assertEquals("Deep dive", saved.getDescription());
    }
}
