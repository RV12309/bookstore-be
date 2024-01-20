package com.hust.bookstore.service.impl;

import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.CategoryRequest;
import com.hust.bookstore.dto.request.SearchCategoryRequest;
import com.hust.bookstore.dto.request.UpdateCategoryRequest;
import com.hust.bookstore.dto.response.CategoryResponse;
import com.hust.bookstore.entity.Category;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.helper.BusinessHelper;
import com.hust.bookstore.repository.*;
import com.hust.bookstore.service.AuthService;
import com.hust.bookstore.service.CategoryService;
import com.hust.bookstore.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hust.bookstore.common.Utils.removeNonUnicodeCharacter;

@Service
@Slf4j
public class CategoryServiceImpl extends BusinessHelper implements CategoryService {


    public CategoryServiceImpl(BookRepository bookRepository, CartRepository cartRepository,
                               CartItemRepository cartItemRepository, PaymentRepository paymentRepository,
                               DeliveryPartnerConfigRepository deliveryPartnerConfigRepo,
                               StoreDeliveryPartnerRepository storeDeliveryPartnerRepo, UserRepository userRepository,
                               OrderDetailRepository orderDetailsRepository, OrderItemsRepository orderItemsRepository,
                               CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository,
                               AccountRepository accountRepository, AuthService authService,
                               BookImageRepository bookImageRepository, ModelMapper modelMapper,
                               NotificationService notificationService, UserAddressRepository addressRepository,
                               DeliveryDetailRepository deliveryDetailRepository) {
        super(bookRepository, cartRepository, cartItemRepository, paymentRepository,
                deliveryPartnerConfigRepo, storeDeliveryPartnerRepo, userRepository,
                orderDetailsRepository, orderItemsRepository, categoryRepository,
                bookCategoryRepository, accountRepository, authService, bookImageRepository,
                modelMapper, notificationService, addressRepository, deliveryDetailRepository);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        String name = request.getName();
        String code = removeNonUnicodeCharacter(name);
        boolean isExist = categoryRepository.existsByCode(code);
        if (isExist) {
            log.error("Category code {} is exist", code);
            throw new BusinessException(ResponseCode.CATEGORY_ALREADY_EXIST);
        }

        Category category = modelMapper.map(request, Category.class);
        category.setCode(code);
        category.setIsDeleted(false);
        Category saved = categoryRepository.save(category);
        return modelMapper.map(saved, CategoryResponse.class);
    }

    @Override
    public CategoryResponse updateCategory(UpdateCategoryRequest request) {
        Long id = request.getId();
        Category category = getExistedCategory(id);
        String name = request.getName();
        String code = removeNonUnicodeCharacter(name);
        boolean isExist = categoryRepository.existsByCodeAndIdNot(code, id);
        if (isExist) {
            log.error("Category code {} is exist", code);
            throw new BusinessException(ResponseCode.CATEGORY_ALREADY_EXIST);
        }

        category.setCode(code);
        category.setName(name);
        category.setDescription(request.getDescription());
        Category saved = categoryRepository.save(category);
        return modelMapper.map(saved, CategoryResponse.class);
    }

    @Override
    public void delete(Long id) {
        Category category = getExistedCategory(id);

        boolean isInUse = bookCategoryRepository.existsByCategoryId(id);
        if (isInUse) {
            log.error("Category {} is in use", category.getName());
            throw new BusinessException(ResponseCode.CATEGORY_IS_IN_USE);
        }
        categoryRepository.delete(category);

    }

    @Override
    public CategoryResponse getDetail(Long id) {
        Category category = getExistedCategory(id);
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(category ->
                modelMapper.map(category, CategoryResponse.class)).toList();
    }

    @Override
    public PageDto<CategoryResponse> searchCategories(SearchCategoryRequest request) {
        String name = StringUtils.isBlank(request.getName()) ? "%" : "%" + request.getName() + "%";
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Category> categories = categoryRepository.findByNameLike(name, pageable);
        List<Category> content = categories.getContent();
        return PageDto.<CategoryResponse>builder()
                .content(content.stream().map(category ->
                        modelMapper.map(category, CategoryResponse.class)).toList())
                .page(categories.getNumber())
                .totalPages(categories.getTotalPages())
                .totalElements(categories.getTotalElements())
                .build();
    }

    private Category getExistedCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new BusinessException(ResponseCode.CATEGORY_NOT_FOUND));
    }
}
