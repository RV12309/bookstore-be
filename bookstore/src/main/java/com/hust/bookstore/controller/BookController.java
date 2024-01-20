package com.hust.bookstore.controller;

import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.BookRequest;
import com.hust.bookstore.dto.request.SearchBookRequest;
import com.hust.bookstore.dto.request.UpdateBookRequest;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.dto.response.BookResponse;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.service.BooksService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
    public ResponseEntity<BaseResponse<BookResponse>> createBook(@Valid @RequestBody BookRequest bookRequest) {
        return ResponseEntity.ok(BaseResponse.<BookResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(booksService.createBook(bookRequest)).build());
    }

    @Operation(summary = "Cập nhật sách")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<BookResponse>> updateBook(@PathVariable Long id,
                                                                 @Valid @RequestBody UpdateBookRequest bookRequest) {
        return ResponseEntity.ok(BaseResponse.<BookResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(booksService.updateBook(bookRequest, id)).build());
    }

    @Operation(summary = "Xóa sách theo mã ISBN")
    @DeleteMapping("/{isbn}")
    public ResponseEntity<BaseResponse<Object>> deleteBook(@PathVariable String isbn) {
        booksService.delete(isbn);
        return ResponseEntity.ok(BaseResponse.builder().code(SUCCESS.code()).message(SUCCESS.message()).build());
    }

    @Operation(summary = "Lấy thông tin chi tiết sách theo mã ISBN")
    @GetMapping("/{isbn}")
    public ResponseEntity<BaseResponse<BookResponse>> getDetail(@PathVariable String isbn) {
        return ResponseEntity.ok(BaseResponse.<BookResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(booksService.getDetail(isbn)).build());
    }

    @Operation(summary = "Tìm kiếm sách")
    @PostMapping("/list")
    public ResponseEntity<BaseResponse<PageDto<BookResponse>>> searchBook(@Valid @RequestBody SearchBookRequest request) {
        return ResponseEntity.ok(BaseResponse.<PageDto<BookResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(booksService.searchBooks(request)).build());
    }

}
