package com.hust.bookstore.serrvice;

import com.hust.bookstore.dto.request.BookRequest;
import com.hust.bookstore.dto.request.SearchBookRequest;
import com.hust.bookstore.dto.request.UpdateBookRequest;
import com.hust.bookstore.dto.response.BookResponse;
import org.springframework.data.domain.Page;

public interface BooksService {
    BookResponse createBook(BookRequest bookRequest);
    BookResponse updateBook(UpdateBookRequest bookRequest);
    void delete(String isbn);
    BookResponse getDetail(String isbn);
    Page<BookResponse> getAllBooks();
    Page<BookResponse> searchBooks( SearchBookRequest request);

}
