package com.hust.bookstore.controller;

import com.hust.bookstore.dto.request.BookRequest;
import com.hust.bookstore.dto.request.SearchBookRequest;
import com.hust.bookstore.dto.request.UpdateBookRequest;
import com.hust.bookstore.dto.response.BookResponse;
import com.hust.bookstore.serrvice.BooksService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/books")
public class BookController {
    private final BooksService booksService;


    public BookController(BooksService booksService) {
        this.booksService = booksService;
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest) {
        return ResponseEntity.ok(booksService.createBook(bookRequest));
    }

    @PutMapping
    public ResponseEntity<BookResponse> updateBook(@Valid @RequestBody UpdateBookRequest bookRequest) {
        return ResponseEntity.ok(booksService.updateBook(bookRequest));
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        booksService.delete(isbn);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponse> getDetail(@PathVariable String isbn) {
        return ResponseEntity.ok(booksService.getDetail(isbn));
    }

    @GetMapping
    public ResponseEntity<Page<BookResponse>> searchBook(@RequestBody SearchBookRequest request) {
        return ResponseEntity.ok(booksService.searchBooks(request));
    }

}
