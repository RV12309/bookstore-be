package com.hust.bookstore.repository;

import com.hust.bookstore.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  Page<Category> findByNameLike(String name, Pageable pageable);

  boolean existsByCode(String code);

  boolean existsByCodeAndIdNot(String code, Long id);
}
