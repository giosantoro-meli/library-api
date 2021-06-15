package com.example.library.service;

import com.example.library.entities.Book;
import com.example.library.exceptions.BusinessException;
import com.example.library.repository.BookRepository;
import com.example.library.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Must save a new book")
    public void SaveBookTest(){
        Book book = buildBook();
        Mockito.when(repository.save(book)).thenReturn(
                Book.builder().id(1L)
                        .isbn("123")
                        .title("As aventuras")
                        .author("Fulano").build()
                );

        Book savedBook = service.save(book);

       assertThat(savedBook.getId()).isNotNull();
       assertThat(savedBook.getIsbn()).isEqualTo("123");
       assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
       assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
    }

    @Test
    @DisplayName("Must throw new Business Exception when Isbn sent is duplicated")
    public void shouldNotSaveBookWithRepeatedIsbn(){
        Book book = buildBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable throwable = Assertions.catchThrowable(() -> service.save(book));

        //verification one
        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn already taken");

        //verification two
        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Must return a Book found by Id")
    public void getByIdTest(){
        Long id = 1L;
        Book book = buildBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Must return that no book was found with that id")
    public void getNonExistingBookByIdTest(){
        Long id = 1L;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> book = service.getById(id);

        assertThat(book.isPresent()).isFalse();
    }

    private Book buildBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

}
