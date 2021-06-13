package com.example.library.repository;

import com.example.library.entities.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest //indicates that will create instance in memory only for testing (then it will delete it)
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Must return true when there is a book with the isbn informed")
    public void returnTrueIfBookWithGivenIsbnExists(){
        String isbn = "123";
        Book book = Book.builder().title("As aventuras").author("Fulano").isbn(isbn).build();
        entityManager.persist(book);
        boolean exists = repository.existsByIsbn(isbn);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Must return false when there is a book with the isbn informed")
    public void returnFalseIfBookWithGivenIsbnDoesNotExist(){
        String isbn = "123";
        boolean exists = repository.existsByIsbn(isbn);
        assertThat(exists).isFalse();
    }
}
