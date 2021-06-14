package com.example.library.service;

import com.example.library.entities.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);
}
