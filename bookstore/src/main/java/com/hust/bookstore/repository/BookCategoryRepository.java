package com.hust.bookstore.repository;

import com.hust.bookstore.entity.BookCategories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCategoryRepository extends JpaRepository<BookCategories, Long> {

    boolean existsByCategoryId(Long id);

    List<BookCategories> findAllByBookIdIn(List<Long> list);
}
