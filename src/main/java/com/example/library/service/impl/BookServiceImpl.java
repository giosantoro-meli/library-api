package com.example.library.service.impl;

import com.example.library.entities.Book;
import com.example.library.exceptions.BusinessException;
import com.example.library.repository.BookRepository;
import com.example.library.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository){
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {

        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("Isbn already taken");
        }

        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return Optional.empty();
    }
}
