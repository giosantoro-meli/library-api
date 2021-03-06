package com.example.library.api.resource;

import com.example.library.api.dto.BookDTO;
import com.example.library.api.dto.LoanDTO;
import com.example.library.api.dto.LoanFilterDTO;
import com.example.library.api.dto.ReturnedLoanDTO;
import com.example.library.entities.Book;
import com.example.library.entities.Loan;
import com.example.library.service.BookService;
import com.example.library.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Api
public class LoanController {

    private final LoanService loanService;

    private final BookService bookService;

    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Loan a book")
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
    @ApiOperation("Return loaned book")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){
        Loan loan = loanService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());

        loanService.update(loan);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Find loan by parameters")
    public Page<LoanDTO> findLoan(LoanFilterDTO filterDTO, Pageable pageRequest){
        Page<Loan> result = loanService.find(filterDTO, pageRequest);

        List<LoanDTO> loanDTOList = result.getContent().stream().map(entity -> {
            Book book = entity.getBook();
            BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
            LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
            loanDTO.setBook(bookDTO);
            return loanDTO;
                }
        ).collect(Collectors.toList());

        return new PageImpl<>(loanDTOList, pageRequest, result.getTotalElements());
    }
}
