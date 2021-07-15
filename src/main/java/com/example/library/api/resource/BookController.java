package com.example.library.api.resource;

import com.example.library.api.dto.BookDTO;
import com.example.library.api.dto.LoanDTO;
import com.example.library.entities.Book;
import com.example.library.entities.Loan;
import com.example.library.service.BookService;
import com.example.library.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/books")
@RequiredArgsConstructor
@Api("Book API")
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create a book")
    public BookDTO create(@RequestBody @Valid BookDTO bookDTO){
        Book book = modelMapper.map(bookDTO, Book.class);
        book = service.save(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @GetMapping(value = "{id}")
    @ApiOperation("Gets a book's details by id")
    public BookDTO getById(@PathVariable Long id){
        return service
                .getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Delete a book by its id")
    @ApiResponses(
            {
                @ApiResponse(code = 204, message = "Book successfully deleted"),
                @ApiResponse(code = 404, message = "Book not found")
            }
    )
    public void delete(@PathVariable Long id){
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

    @PutMapping(value = "{id}")
    @ApiOperation("Update book info")
    public BookDTO update(@PathVariable Long id, @RequestBody @Valid BookDTO bookDTO){
        return service.getById(id).map( book -> {
            book.setAuthor(bookDTO.getAuthor());
            book.setTitle(bookDTO.getTitle());
            service.update(book);
            return modelMapper.map(book, BookDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @ApiOperation("Find books with parameters")
    public Page<BookDTO> find(BookDTO bookDTO, Pageable pageRequest){
        Book filter = modelMapper.map(bookDTO, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDTO> list = result.getContent().stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }

    @GetMapping(value = "{id}/loans")
    @ApiOperation("Find loans of book")
    public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable p){
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoansByBook(book, p);

        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);

        List<LoanDTO> list = result.getContent().stream()
                .map(entity -> {
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(list, p, result.getTotalElements());
    }
}
