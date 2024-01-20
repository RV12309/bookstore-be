package com.hust.bookstore.repository;

import com.hust.bookstore.dto.request.SearchBookRequest;
import com.hust.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
  Page<Book> findAllByTitleContaining(String title, Pageable pageable);

  Optional<Book> findByIsbn(String isbn);



  @Query("SELECT b FROM Book b " +
          "WHERE " +
          "b.isDeleted = false "+
          "AND b.title LIKE :#{#input.getTitle()} " +
          "AND b.author LIKE :#{#input.getAuthor()} " +
          "AND (:#{#input.getPriceFrom()} IS NULL OR b.price >= :#{#input.getPriceFrom()}) " +
          "AND (:#{#input.getPriceTo()} IS NULL OR b.price <= :#{#input.getPriceTo()}) " +
          "AND (coalesce(:#{#input.getCategoryIds()}) is null or " +
          "b.id in (SELECT bc.bookId FROM BookCategories bc WHERE bc.categoryId in :#{#input.getCategoryIds()}))")
  Page<Book> searchBooks(SearchBookRequest input, Pageable pageable);
}
