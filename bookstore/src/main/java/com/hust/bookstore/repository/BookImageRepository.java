package com.hust.bookstore.repository;

import com.hust.bookstore.entity.BookImages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookImageRepository extends JpaRepository<BookImages, Long> {
}
