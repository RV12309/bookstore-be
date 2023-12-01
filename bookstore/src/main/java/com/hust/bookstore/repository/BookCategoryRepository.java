package com.hust.bookstore.repository;

import com.hust.bookstore.entity.BookCategories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCategoryRepository extends JpaRepository<BookCategories, Long> {

    boolean existsByCategoryId(Long id);
}
