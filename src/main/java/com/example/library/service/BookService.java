package com.example.library.service;

import com.example.library.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Book book, Pageable page);

    Optional<Book> getBookByIsbn(String isbn);
}
