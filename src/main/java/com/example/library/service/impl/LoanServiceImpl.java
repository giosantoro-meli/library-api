package com.example.library.service.impl;

import com.example.library.entities.Loan;
import com.example.library.repository.LoanRepository;
import com.example.library.service.LoanService;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository){
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        return loanRepository.save(loan);
    }
}
