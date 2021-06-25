package com.example.library.service;

import com.example.library.entities.Book;
import com.example.library.entities.Loan;
import com.example.library.exceptions.BusinessException;
import com.example.library.repository.LoanRepository;
import com.example.library.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    LoanRepository loanRepository;
    LoanService loanService;

    @BeforeEach
    public void setUp(){
        this.loanService = new LoanServiceImpl(loanRepository);
    }

    @Test
    @DisplayName("Must save a new loan")
    public void saveLoanTest(){

        Book book = Book.builder().id(1L).build();

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .id(1L)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(1L)
                .loanDate(LocalDate.now())
                .customer("Fulano")
                .book(book)
                .build();

        when(loanRepository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = loanService.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    @DisplayName("Must throw a BusinessException when book is already taken")
    public void tryToBorrowTakenBookTest(){

        Loan savingLoan = createLoan();
        Book book = savingLoan.getBook();

        when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable throwable = catchThrowable(() -> loanService.save(savingLoan));

        assertThat(throwable).isInstanceOf(BusinessException.class)
            .hasMessage("Book already borrowed");

        verify(loanRepository, never()).save(savingLoan);
    }

    @Test
    @DisplayName("Must return a loan by its id")
    public void getLoanByIdTest(){
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = loanService.getById(id);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(loanRepository).findById(id);
    }

    public static Loan createLoan(){
        Book book = Book.builder().id(1L).build();

        return Loan.builder()
                .book(book)
                .customer("Fulano")
                .id(1L)
                .loanDate(LocalDate.now())
                .build();
    }
}
