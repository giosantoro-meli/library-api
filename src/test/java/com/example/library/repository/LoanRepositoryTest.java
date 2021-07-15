package com.example.library.repository;

import com.example.library.entities.Book;
import com.example.library.entities.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static com.example.library.repository.BookRepositoryTest.buildBook;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository loanRepository;

    @Test
    @DisplayName("Must verify if book exists and if it is not returned")
    public void existsByBookAndNotReturnedTest(){

        Loan loan = createAndPersistLoan(LocalDate.now());

        boolean exists = loanRepository.existsByBookAndNotReturned(loan.getBook());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Must find a loan by book isbn or customer")
    public void findBookByIsbnOrCustomerTest(){

        Loan loan = createAndPersistLoan(LocalDate.now());

        Page<Loan> result = loanRepository.findByBookIsbnOrCustomer("123", "Fulano", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Must return a list of all current delayed loans for more than 3 days")
    public void fiindAllLoansDelayedMoreThanThreeDays(){
        Loan loan = createAndPersistLoan(LocalDate.now().minusDays(5));

        List<Loan> result = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("Must not return any loan delayed")
    public void noBookDelayedMoreThanThreeDays(){
        Loan loan = createAndPersistLoan(LocalDate.now());

        List<Loan> result = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).isEmpty();
    }

    private Loan createAndPersistLoan(LocalDate localDate){
        Book book = buildBook("123");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(localDate).build();

        entityManager.persist(loan);

        return loan;
    }
}
