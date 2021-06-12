package com.example.library.api.resource;

import com.example.library.api.dto.BookDTO;
import com.example.library.entities.Book;
import com.example.library.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO bookDTO){
        Book book = modelMapper.map(bookDTO, Book.class);

        book = service.save(book);

        return modelMapper.map(book, BookDTO.class);
    }
}
