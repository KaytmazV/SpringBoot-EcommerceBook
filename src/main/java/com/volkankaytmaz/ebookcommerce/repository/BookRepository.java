package com.volkankaytmaz.ebookcommerce.repository;

import com.volkankaytmaz.ebookcommerce.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
    long count();
    List<BookEntity> findByAuthor(String author);
    List<BookEntity> findByTitleContainingIgnoreCase(String title);
}

