package com.example.library.api.resource;

import com.example.library.api.dto.LoanDTO;
import com.example.library.api.dto.ReturnedLoanDTO;
import com.example.library.entities.Book;
import com.example.library.entities.Loan;
import com.example.library.exceptions.BusinessException;
import com.example.library.service.BookService;
import com.example.library.service.LoanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Properties;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest (controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";
    static final String isbn = "123";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Must perform a loan")
    public void createLoanTest() throws Exception{

        LoanDTO dto = LoanDTO.builder().isbn(isbn).customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Book book = Book.builder().id(1L).isbn(isbn).build();
        Loan loan = Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now()).build();

        BDDMockito.given(bookService.getBookByIsbn(isbn)).willReturn(Optional.of(book));
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect( content().string("1") );
    }

    @Test
    @DisplayName("Must throw exception saying no book with informed ISBN exists")
    public void invalidIsbnCreateLoanTest() throws Exception{
        LoanDTO dto = LoanDTO.builder().isbn(isbn).customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn(isbn)).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
    }

    @Test
    @DisplayName("Must throw exception saying no book is not available for borrow")
    public void bookIsAlreadyTakenTest() throws Exception{
        LoanDTO dto = LoanDTO.builder().isbn(isbn).customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn(isbn).build();
        BDDMockito.given(bookService.getBookByIsbn(isbn)).willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book already borrowed"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already borrowed"));
    }

    @Test
    @DisplayName("Must return a borrowed book")
    public void returnBookTest() throws Exception {
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        Loan loan = Loan.builder().id(1L).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk());

        verify(loanService, times(1)).update(loan);
    }
}
