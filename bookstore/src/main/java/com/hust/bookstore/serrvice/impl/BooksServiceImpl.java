package com.hust.bookstore.serrvice.impl;

import com.hust.bookstore.dto.request.BookRequest;
import com.hust.bookstore.dto.request.SearchBookRequest;
import com.hust.bookstore.dto.request.UpdateBookRequest;
import com.hust.bookstore.dto.response.BookResponse;
import com.hust.bookstore.entity.*;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.repository.*;
import com.hust.bookstore.serrvice.AuthService;
import com.hust.bookstore.serrvice.BooksService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hust.bookstore.common.Utils.generateIsbn;

@Service
@Slf4j
public class BooksServiceImpl implements BooksService {
    private final BookRepository bookRepository;

    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;

    private final CategoryRepository categoryRepository;

    private final AuthService authService;

    private final BookImageRepository bookImageRepository;

    private final BookCategoryRepository bookCategoryRepository;


    public BooksServiceImpl(BookRepository bookRepository, ModelMapper modelMapper,
                            AccountRepository accountRepository, CategoryRepository categoryRepository,
                            AuthService authService, BookImageRepository bookImageRepository,
                            BookCategoryRepository bookCategoryRepository) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.authService = authService;
        this.bookImageRepository = bookImageRepository;
        this.bookCategoryRepository = bookCategoryRepository;
    }

    @Override
    @Transactional
    public BookResponse createBook(BookRequest bookRequest) {
        Account currentAccount = authService.getCurrentAccountLogin();
        if (currentAccount == null)
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_FOUND);

        Book book = modelMapper.map(bookRequest, Book.class);

        List<Long> categoryIds = bookRequest.getCategoryIds();

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size())
            throw new BusinessException(ResponseCode.CATEGORY_NOT_FOUND);

        List<String> imageUrls = bookRequest.getImagesUrls();
        if (!CollectionUtils.isEmpty(imageUrls)) {
            log.info("Saving images for book {}.", book.getIsbn());
            List<BookImages> bookImages = new ArrayList<>();
            for (String url : imageUrls) {
                BookImages bookImage = BookImages.builder().url(url).book(book).build();
                bookImages.add(bookImage);
            }
            bookImageRepository.saveAll(bookImages);
            log.info("Saved images for book {}.", book.getIsbn());
        }

        book.setAccountId(currentAccount.getId());

        book.setIsbn(generateIsbn());

        Book saved = bookRepository.save(book);
        log.info("Saved book {}.", saved.getIsbn());

        if (!CollectionUtils.isEmpty(categories)) {
            log.info("Saving categories for book {}.", saved.getIsbn());
            List<BookCategories> bookCategories = new ArrayList<>();
            for (Category category : categories) {
                BookCategories bookCategory = BookCategories.builder()
                        .bookId(saved.getId())
                        .categoryId(category.getId()).build();
                bookCategories.add(bookCategory);
            }
            bookCategoryRepository.saveAll(bookCategories);
            log.info("Saved categories for book {}.", saved.getIsbn());
        }

        log.info("Saved book successfully.");
        return modelMapper.map(saved, BookResponse.class);
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
            sort = List.of("title");
        }

        Sort sortBy = Sort.by(sort.stream().map(Sort.Order::by).toList());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize()).withSort(sortBy);
        Page<Book> books = bookRepository.searchBooks(request, pageable);
        return books.map(book -> modelMapper.map(book, BookResponse.class));
    }
}
