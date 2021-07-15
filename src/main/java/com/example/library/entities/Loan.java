package com.example.library.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customer;

    @Column(name="customer_email")
    private String customerEmail;
    private LocalDate loanDate;

    @JoinColumn(name = "book_id")
    @ManyToOne
    private Book book;
    private Boolean returned;
}
