package com.example.library.api.resource;

import com.example.library.api.dto.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/books")
public class BookController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(){
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Moby Dick");
        bookDTO.setAuthor("Herman Melville");
        bookDTO.setIsbn("9788575036709");
        bookDTO.setId(1L);

        return bookDTO;
    }
}
