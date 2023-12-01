package com.hust.bookstore.repository;

import com.hust.bookstore.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  Page<Category> findByNameLike(String name, Pageable pageable);

  boolean existsByCode(String code);

  boolean existsByCodeAndIdNot(String code, Long id);

  @Query("select c from Category c join BookCategories bc on c.id = bc.categoryId where bc.bookId = :id")
    List<Category> findCategories(Long id);
}
