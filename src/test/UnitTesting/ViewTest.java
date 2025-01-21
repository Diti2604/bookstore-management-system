package test.UnitTesting;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import Controller.CreateBillController;
import Model.Book;
import View.AddBookView;
import View.BillView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ViewTest {
    private AddBookView addBookView;
    private CreateBillController createBillControllerMock;
    private BillView billView;
    private List<Book> books;
    private String librarianUsername = "librarian123";

    @BeforeEach
    void setUp() {
        addBookView = new AddBookView();
        books = new ArrayList<>();
        Connection connectionMock = mock(Connection.class);
        createBillControllerMock = spy(new CreateBillController(connectionMock));
        billView = new BillView();
        BillView.setLibrarianUsername(librarianUsername);

    }

    @Test
    void testValidateISBNValidFormat() {
        assertTrue(addBookView.validateISBN("1234-56-7890"), "Valid ISBN format failed validation");
        assertTrue(addBookView.validateISBN("5678-12-3456"), "Valid ISBN format failed validation");
    }

    @Test
    void testValidateISBNInvalidFormat() {
        assertFalse(addBookView.validateISBN("1234567890"), "Invalid ISBN format passed validation");
        assertFalse(addBookView.validateISBN("123-45-6789"), "Invalid ISBN format passed validation");
        assertFalse(addBookView.validateISBN("ABCD-56-7890"), "Invalid ISBN format passed validation");
        assertFalse(addBookView.validateISBN("1234-567890"), "Invalid ISBN format passed validation");
    }

    @Test
    void testValidateISBNEmpty() {
        assertFalse(addBookView.validateISBN(""), "Empty ISBN passed validation");
    }


    @Test
    void testValidateAuthorValidNames() {
        assertTrue(addBookView.validateAuthor("John Doe"), "Valid author name failed validation");
        assertTrue(addBookView.validateAuthor("Alice"), "Valid author name failed validation");
        assertTrue(addBookView.validateAuthor("Marie Curie"), "Valid author name failed validation");
    }

    @Test
    void testValidateAuthorInvalidNames() {
        assertFalse(addBookView.validateAuthor("John123"), "Invalid author name passed validation");
        assertFalse(addBookView.validateAuthor("Jane_Doe"), "Invalid author name passed validation");
        assertFalse(addBookView.validateAuthor("!@#$%^"), "Invalid author name passed validation");
        assertFalse(addBookView.validateAuthor("John-Doe"), "Invalid author name passed validation");
    }

    @Test
    void testValidateAuthorEmpty() {
        assertFalse(addBookView.validateAuthor(""), "Empty author name passed validation");
        assertThrows(NullPointerException.class, () -> addBookView.validateAuthor(null), "Null author name did not throw exception");
    }


    @Test
    void testProcessAddedBooks() {
        books.add(new Book("https://books.com", "1984", "FICTION", "2222-22-2222", "AUTHOR 1", 5));
        books.add(new Book("https://books.com", "Harry Potter", "FICTION", "1111-11-1111", "AUTHOR 2", 3));
        billView.processAddedBooks();
        verify(createBillControllerMock).saveBillToDatabase(librarianUsername, "1984", 5, 25.0);
        verify(createBillControllerMock).saveBillToDatabase(librarianUsername, "Harry Potter", 3, 9.0);
    }

    @Test
    void testProcessAddedBooksEmptyList() {
        billView.processAddedBooks();
        verify(createBillControllerMock, never()).saveBillToDatabase(anyString(), anyString(), anyInt(), anyDouble());
    }

    @Test
    void testProcessAddedBooksExceptionHandling() {
        books.add(new Book("https://books.com", "Book A", "FICTION", "2222-22-2222", "AUTHOR 1", 5));
        doThrow(new RuntimeException("Database error")).when(createBillControllerMock)
                .saveBillToDatabase(anyString(), anyString(), anyInt(), anyDouble());
        assertDoesNotThrow(() -> billView.processAddedBooks());
        verify(createBillControllerMock).saveBillToDatabase(librarianUsername, "Book A", 5, 10.0);
    }


}