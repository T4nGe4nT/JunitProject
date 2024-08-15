package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    private BookService bookService;
    private User mockUser;
    private Book mockBook;

    @BeforeEach
    public void setUp() {
        bookService = new BookService();
        mockUser = Mockito.mock(User.class);
        mockBook = Mockito.mock(Book.class);
    }

    // --------------- Tests for searchBook() ---------------

    @Test
    public void testSearchBook_Positive() {
        // Setup: Add books to the bookDatabase
        Book book1 = new Book("Title1", "Author1", "Genre1", 10.99);
        Book book2 = new Book("KeywordTitle", "Author2", "Genre2", 12.99);
        Book book3 = new Book("Title3", "KeywordAuthor", "Genre3", 15.99);
        bookService.addBook(book1);
        bookService.addBook(book2);
        bookService.addBook(book3);

        // Search for books with the keyword "Keyword"
        List<Book> result = bookService.searchBook("Keyword");

        // Assert that two books match the keyword search
        assertEquals(2, result.size());
        assertTrue(result.contains(book2));
        assertTrue(result.contains(book3));
    }

    @Test
    public void testSearchBook_NoMatch() {
        // Setup: Add books to the bookDatabase
        Book book1 = new Book("Title1", "Author1", "Genre1", 10.99);
        bookService.addBook(book1);

        // Search for books with a keyword that doesn't match any book
        List<Book> result = bookService.searchBook("NonExistentKeyword");

        // Assert that no books are returned
        assertEquals(0, result.size());
    }

    @Test
    public void testSearchBook_EmptyKeyword() {
        // Setup: Add books to the bookDatabase
        Book book1 = new Book("Title1", "Author1", "Genre1", 10.99);
        bookService.addBook(book1);

        // Search with an empty keyword
        List<Book> result = bookService.searchBook("");

        // Assert that all books are returned
        assertEquals(1, result.size());
        assertTrue(result.contains(book1));
    }

    @Test
    public void testSearchBook_SpecialCharactersInKeyword() {
        // Setup: Add books to the bookDatabase
        Book book1 = new Book("Title@!", "Author1", "Genre1", 10.99);
        bookService.addBook(book1);

        // Search for books with special characters
        List<Book> result = bookService.searchBook("@!");

        // Assert that the book with special characters is returned
        assertEquals(1, result.size());
        assertTrue(result.contains(book1));
    }

    // --------------- Tests for purchaseBook() ---------------

    @Test
    public void testPurchaseBook_Positive() {
        // Setup: Add a book to the bookDatabase
        Book book = new Book("Title1", "Author1", "Genre1", 10.99);
        bookService.addBook(book);

        // Assert that purchase is successful for a book in the database
        assertTrue(bookService.purchaseBook(mockUser, book));
    }

    @Test
    public void testPurchaseBook_Negative() {
        // Setup: Book is not added to the database
        Book book = new Book("Title1", "Author1", "Genre1", 10.99);

        // Assert that purchase fails for a book not in the database
        assertFalse(bookService.purchaseBook(mockUser, book));
    }

    @Test
    public void testPurchaseBook_NullBook() {
        // Assert that purchase fails when the book is null
        assertFalse(bookService.purchaseBook(mockUser, null));
    }

    // --------------- Tests for addBookReview() ---------------

    @Test
    public void testAddBookReview_Positive() {
        // Setup: Create a mock Book
        Book book = mock(Book.class);

        // Setup: User has purchased the book
        when(mockUser.getPurchasedBooks()).thenReturn(Arrays.asList(book));

        // Setup: Mock the behavior of the getReviews method
        List<String> mockReviews = mock(List.class);  // Mock the List
        when(book.getReviews()).thenReturn(mockReviews);

        // Add a review
        boolean result = bookService.addBookReview(mockUser, book, "Great book!");

        // Assert that the review was added successfully
        assertTrue(result);

        // Verify that the add method was called on the mocked List
        verify(mockReviews).add("Great book!");
    }

    @Test
    public void testAddBookReview_Negative_NotPurchased() {
        // Setup: User has not purchased the book
        Book book = new Book("Title1", "Author1", "Genre1", 10.99);
        when(mockUser.getPurchasedBooks()).thenReturn(Arrays.asList());

        // Add a review
        boolean result = bookService.addBookReview(mockUser, book, "Great book!");

        // Assert that the review was not added
        assertFalse(result);
    }

    @Test
    public void testAddBookReview_NullReview() {
        // Setup: User has purchased the book
        Book book = mock(Book.class);
        when(mockUser.getPurchasedBooks()).thenReturn(Arrays.asList(book));

        // Setup: Mock the behavior of the getReviews method
        List<String> mockReviews = mock(List.class);
        when(book.getReviews()).thenReturn(mockReviews);

        // Try adding a null review
        boolean result = bookService.addBookReview(mockUser, book, null);

        // Since BookService does not handle null reviews, we expect this to pass as true
        assertTrue(result);

        // Verify that the add method was called on the mocked List
        verify(mockReviews).add(null);
    }

    // --------------- Tests for addBook() ---------------

    @Test
    public void testAddBook_Positive() {
        // Assert that adding a new book is successful
        Book book = new Book("Title1", "Author1", "Genre1", 10.99);
        assertTrue(bookService.addBook(book));
    }

    @Test
    public void testAddBook_Negative() {
        // Setup: Add the book to the database first
        Book book = new Book("Title1", "Author1", "Genre1", 10.99);
        bookService.addBook(book);

        // Assert that adding the same book again fails
        assertFalse(bookService.addBook(book));
    }

    @Test
    public void testAddBook_NullBook() {
        // Since BookService does not handle null books, we expect this to pass as true
        assertTrue(bookService.addBook(null));
    }

    // --------------- Tests for removeBook() ---------------

    @Test
    public void testRemoveBook_Positive() {
        // Setup: Add the book to the database
        Book book = new Book("Title1", "Author1", "Genre1", 10.99);
        bookService.addBook(book);

        // Assert that removing the book is successful
        assertTrue(bookService.removeBook(book));
    }

    @Test
    public void testRemoveBook_Negative() {
        // Setup: Book is not added to the database
        Book book = new Book("Title1", "Author1", "Genre1", 10.99);

        // Assert that removing a book not in the database fails
        assertFalse(bookService.removeBook(book));
    }

    @Test
    public void testRemoveBook_NullBook() {
        // Assert that removing a null book fails
        assertFalse(bookService.removeBook(null));
    }

    @Test
    public void testRemoveBook_EmptyDatabase() {
        // Setup: Ensure the book database is empty
        Book book = new Book("Title1", "Author1", "Genre1", 10.99);

        // Assert that removing a book from an empty database fails
        assertFalse(bookService.removeBook(book));
    }
}


