package com.hust.bookstore.controller;

import com.hust.bookstore.dto.request.BookRequest;
import com.hust.bookstore.dto.request.SearchBookRequest;
import com.hust.bookstore.dto.request.UpdateBookRequest;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.dto.response.BookResponse;
import com.hust.bookstore.serrvice.BooksService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hust.bookstore.enumration.ResponseCode.SUCCESS;

@RestController
@RequestMapping("/v1/books")
public class BookController {
    private final BooksService booksService;


    public BookController(BooksService booksService) {
        this.booksService = booksService;
    }

    @Operation(summary = "Thêm mới sách")
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest) {
        return ResponseEntity.ok(booksService.createBook(bookRequest));
    }

    @Operation(summary = "Cập nhật sách")
    @PutMapping
    public ResponseEntity<BookResponse> updateBook(@Valid @RequestBody UpdateBookRequest bookRequest) {
        return ResponseEntity.ok(booksService.updateBook(bookRequest));
    }

    @Operation(summary = "Xóa sách theo mã ISBN")
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Object> deleteBook(@PathVariable String isbn) {
        booksService.delete(isbn);
        return ResponseEntity.ok(BaseResponse.builder().code(SUCCESS.code()).message(SUCCESS.message()).build());
    }

    @Operation(summary = "Lấy thông tin chi tiết sách theo mã ISBN")
    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponse> getDetail(@PathVariable String isbn) {
        return ResponseEntity.ok(booksService.getDetail(isbn));
    }

    @Operation(summary = "Tìm kiếm sách")
    @GetMapping
    public ResponseEntity<Page<BookResponse>> searchBook(@RequestBody SearchBookRequest request) {
        return ResponseEntity.ok(booksService.searchBooks(request));
    }

}
