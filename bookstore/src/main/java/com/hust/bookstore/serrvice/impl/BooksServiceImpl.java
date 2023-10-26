package com.hust.bookstore.serrvice.impl;

import com.hust.bookstore.dto.request.BookRequest;
import com.hust.bookstore.dto.request.SearchBookRequest;
import com.hust.bookstore.dto.request.UpdateBookRequest;
import com.hust.bookstore.dto.response.BookResponse;
import com.hust.bookstore.entity.Book;
import com.hust.bookstore.entity.BookImages;
import com.hust.bookstore.entity.Category;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.repository.AccountRepository;
import com.hust.bookstore.repository.BookRepository;
import com.hust.bookstore.repository.CategoryRepository;
import com.hust.bookstore.serrvice.BooksService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BooksServiceImpl implements BooksService {
    private final BookRepository bookRepository;

    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;

    private final CategoryRepository categoryRepository;

    public BooksServiceImpl(BookRepository bookRepository, ModelMapper modelMapper,
                            AccountRepository accountRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public BookResponse createBook(BookRequest bookRequest) {
        Book book = modelMapper.map(bookRequest, Book.class);

        List<Long> categoryIds = bookRequest.getCategoryIds();

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size())
            throw new BusinessException(ResponseCode.CATEGORY_NOT_FOUND);
        book.setCategories(categories);

        List<String> imageUrls = bookRequest.getImagesUrls();
        if (!CollectionUtils.isEmpty(imageUrls)) {
            for (String url : imageUrls) {
                BookImages bookImages = BookImages.builder().url(url).book(book).build();
                book.getBookImages().add(bookImages);
            }
        }

        //ToDo: add account id

        bookRepository.save(book);
        return modelMapper.map(book, BookResponse.class);
    }

    @Override
    public BookResponse updateBook(UpdateBookRequest request) {
        Book book = bookRepository.getReferenceById(request.getId());
        modelMapper.map(request, book);
        bookRepository.save(book);
        return modelMapper.map(book, BookResponse.class);
    }

    @Override
    public void delete(String isbn) {
        Book book = getBook(isbn);
        book.setIsDeleted(true);
        bookRepository.save(book);
    }

    private Book getBook(String isbn) {
        return bookRepository.findByIsbn(isbn).orElseThrow(() -> new BusinessException(ResponseCode.BOOK_NOT_FOUND));
    }

    @Override
    public BookResponse getDetail(String isbn) {
        Book book = getBook(isbn);
        if (Boolean.TRUE.equals(book.getIsDeleted()))
            throw new BusinessException(ResponseCode.BOOK_IS_DELETED);
        return modelMapper.map(book, BookResponse.class);
    }

    @Override
    public Page<BookResponse> getAllBooks() {
        Pageable pageable = Pageable.unpaged();
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(book -> modelMapper.map(book, BookResponse.class));
    }

    @Override
    public Page<BookResponse> searchBooks(SearchBookRequest request) {
        String title = StringUtils.isBlank(request.getTitle()) ? "%" : "%" + request.getTitle() + "%";
        String author = StringUtils.isBlank(request.getAuthor()) ? "%" : "%" + request.getAuthor() + "%";
        request.setTitle(title);
        request.setAuthor(author);

        List<String> sort = request.getSort();
        if (CollectionUtils.isEmpty(sort)) {
            sort.add("title");
        }

        Sort sortBy = Sort.by(request.getSort().stream().map(Sort.Order::by).collect(Collectors.toList()));

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize()).withSort(sortBy);
        Page<Book> books = bookRepository.searchBooks(request, pageable);
        return books.map(book -> modelMapper.map(book, BookResponse.class));
    }
}
