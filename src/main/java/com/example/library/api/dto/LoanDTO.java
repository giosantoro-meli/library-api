package com.example.library.api.dto;

import com.example.library.entities.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private String isbn;
    private String customer;
    private BookDTO book;
}
