package com.example.library.service.impl;

import com.example.library.entities.Book;
import com.example.library.exceptions.BusinessException;
import com.example.library.repository.BookRepository;
import com.example.library.service.BookService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

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
        return repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if(book == null || book.getId() == null){
            throw new IllegalArgumentException("You cannot delete a non existent book");
        }
        repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if(book == null || book.getId() == null){
            throw new IllegalArgumentException("You cannot update a non existent book");
        }
        return repository.save(book);
    }

    @Override
    public Page<Book> find(Book book, Pageable page) {
        Example<Book> example = Example.of(book,
                ExampleMatcher.matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example, page);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return repository.findByIsbn(isbn);
    }

}
