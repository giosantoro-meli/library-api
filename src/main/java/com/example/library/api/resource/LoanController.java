package com.example.library.api.resource;

import com.example.library.api.dto.LoanDTO;
import com.example.library.api.dto.ReturnedLoanDTO;
import com.example.library.entities.Book;
import com.example.library.entities.Loan;
import com.example.library.service.BookService;
import com.example.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    private final BookService bookService;

    public LoanController(LoanService loanService, BookService bookService) {
        this.loanService = loanService;
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO){

        Book book = bookService.getBookByIsbn(loanDTO.getIsbn()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn")
        );
        Loan entity = Loan.builder().book(book).customer(loanDTO.getCustomer())
                .loanDate(LocalDate.now()).build();

        entity = loanService.save(entity);

        return entity.getId();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){
        Loan loan = loanService.getById(id).get();
        loan.setReturned(dto.getReturned());

        loanService.update(loan);
    }
}
