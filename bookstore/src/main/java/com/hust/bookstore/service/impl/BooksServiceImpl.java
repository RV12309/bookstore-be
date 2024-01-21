package com.hust.bookstore.service.impl;

import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.BookRequest;
import com.hust.bookstore.dto.request.SearchBookRequest;
import com.hust.bookstore.dto.request.UpdateBookRequest;
import com.hust.bookstore.dto.response.BookResponse;
import com.hust.bookstore.dto.response.CategoryResponse;
import com.hust.bookstore.entity.*;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.enumration.UserType;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.helper.BusinessHelper;
import com.hust.bookstore.repository.*;
import com.hust.bookstore.service.AuthService;
import com.hust.bookstore.service.BooksService;
import com.hust.bookstore.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.hust.bookstore.common.Utils.generateIsbn;

@Service
@Slf4j
public class BooksServiceImpl extends BusinessHelper implements BooksService {


    public BooksServiceImpl(BookRepository bookRepository, CartRepository cartRepository, CartItemRepository cartItemRepository,
                            PaymentRepository paymentRepository, DeliveryPartnerConfigRepository deliveryPartnerConfigRepo,
                            StoreDeliveryPartnerRepository storeDeliveryPartnerRepo, UserRepository userRepository,
                            OrderDetailRepository orderDetailsRepository, OrderItemsRepository orderItemsRepository,
                            CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository,
                            AccountRepository accountRepository, AuthService authService, BookImageRepository bookImageRepository,
                            ModelMapper modelMapper, NotificationService notificationService,
                            UserAddressRepository addressRepository, DeliveryDetailRepository deliveryDetailRepository) {
        super(bookRepository, cartRepository, cartItemRepository, paymentRepository,
                deliveryPartnerConfigRepo, storeDeliveryPartnerRepo, userRepository,
                orderDetailsRepository, orderItemsRepository, categoryRepository,
                bookCategoryRepository, accountRepository, authService, bookImageRepository, modelMapper,
                notificationService, addressRepository, deliveryDetailRepository);
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
                BookImages bookImage = BookImages.builder().url(url)
                        .bookId(book.getId()).build();
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
    public BookResponse updateBook(UpdateBookRequest request, Long id) {
        log.info("Updating book {}.", id);
        Book book = checkExistBook(id);
        modelMapper.map(request, book);
        bookRepository.save(book);
        log.info("Updated book {}.", id);
        return modelMapper.map(book, BookResponse.class);
    }

    @Override
    public void delete(String isbn) {
        log.info("Deleting book {}.", isbn);
        Book book = getBook(isbn);
        book.setIsDeleted(true);
        bookRepository.save(book);
        log.info("Deleted book {}.", isbn);
    }

    private Book getBook(String isbn) {
        return bookRepository.findByIsbn(isbn).orElseThrow(() -> new BusinessException(ResponseCode.BOOK_NOT_FOUND));
    }

    @Override
    public BookResponse getDetail(String isbn) {
        log.info("Getting detail of book {}.", isbn);
        Book book = getBook(isbn);
        if (Boolean.TRUE.equals(book.getIsDeleted()))
            throw new BusinessException(ResponseCode.BOOK_IS_DELETED);
        log.info("Got detail of book {}.", isbn);
        List<Category> categories = categoryRepository.findCategories(book.getId());
        List<CategoryResponse> categoryResponses =
                categories.stream().map(category -> modelMapper.map(category, CategoryResponse.class)).toList();
        log.info("Got {} categories of book {}.", categoryResponses.size(), isbn);
        BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
        bookResponse.setCategories(categoryResponses);
        SellerProjection seller = userRepository.findSeller(book.getAccountId()).orElse(null);
        if (seller != null) {
            bookResponse.setSellerId(String.valueOf(seller.getId()));
            bookResponse.setSellerName(seller.getName());
        }
        return bookResponse;
    }

    @Override
    public Page<BookResponse> getAllBooks() {
        log.info("Getting all books.");
        Pageable pageable = Pageable.unpaged();
        Page<Book> books = bookRepository.findAll(pageable);
        log.info("Got {} books.", books.getTotalElements());
        return books.map(book -> modelMapper.map(book, BookResponse.class));
    }

    @Override
    public PageDto<BookResponse> searchBooks(SearchBookRequest request) {
        String title = StringUtils.isBlank(request.getTitle()) ? "%" : "%" + request.getTitle() + "%";
        String author = StringUtils.isBlank(request.getAuthor()) ? "%" : "%" + request.getAuthor() + "%";
        request.setTitle(title);
        request.setAuthor(author);
        Account currentAccount = authService.getCurrentAccountLogin();
        Long id = null;

        if (currentAccount != null) {
            Account account = accountRepository.findById(currentAccount.getId()).orElse(null);
            if (account != null && (account.getType() == UserType.SELLER)) {
                    id = account.getId();

            }
        }

        List<String> sort = request.getSort();
        if (CollectionUtils.isEmpty(sort)) {
            sort = List.of("title");
        }
        log.info("Searching books with request {}.", request);

        Sort sortBy = Sort.by(sort.stream().map(Sort.Order::by).toList());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize()).withSort(sortBy);
        Page<Book> books = bookRepository.searchBooks(request, id, pageable);
        List<Book> content = books.getContent();
        List<Long> accountIds = content.stream().map(Book::getAccountId).distinct().toList();
        Map<Long, String> accountMap = new HashMap<>();
        if (non(CollectionUtils.isEmpty(accountIds))) {
            List<SellerProjection> accounts = userRepository.findAllByAccountIdIn(accountIds);
            //filter null
            accounts = accounts.stream().filter(Objects::nonNull).toList();
            if (non(CollectionUtils.isEmpty(accounts)))
                for (SellerProjection account : accounts) {
                    if (account != null && account.getId() != null && account.getName() != null)
                        accountMap.put(account.getId(), account.getName());
                }
        }

        List<BookCategories> bookCategories
                = bookCategoryRepository.findAllByBookIdIn(content.stream().map(Book::getId).toList());
        List<Category> categories = categoryRepository.findAllById(bookCategories.stream().map(BookCategories::getCategoryId).toList());
        Map<Long, List<Category>> categoryMap = new HashMap<>();
        if (non(CollectionUtils.isEmpty(categories))) {
            for (Category category : categories) {
                if (category != null && category.getId() != null) {
                    if (categoryMap.containsKey(category.getId())) {
                        categoryMap.get(category.getId()).add(category);
                    } else {
                        categoryMap.put(category.getId(), List.of(category));
                    }
                }
            }
        }

        List<BookResponse> bookResponses = content.stream().map(book -> {
            BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
            bookResponse.setSellerId(String.valueOf(book.getAccountId()));
            if (accountMap.containsKey(book.getAccountId()))
                bookResponse.setSellerName(accountMap.get(book.getAccountId()));
            if (categoryMap.containsKey(book.getId())) {
                List<Category> categoryList = categoryMap.get(book.getId());
                List<CategoryResponse> categoryResponses = categoryList.stream()
                        .map(category -> modelMapper.map(category, CategoryResponse.class)).toList();
                bookResponse.setCategories(categoryResponses);
            }
            return bookResponse;
        }).toList();

        log.info("Found {} books.", bookResponses.size());
        return PageDto.<BookResponse>builder().content(bookResponses)
                .totalElements(books.getTotalElements())
                .totalPages(books.getTotalPages())
                .page(books.getNumber())
                .size(books.getSize())
                .build();
    }
}
