package com.example.library.service.impl;

import com.example.library.api.dto.LoanFilterDTO;
import com.example.library.entities.Loan;
import com.example.library.exceptions.BusinessException;
import com.example.library.repository.LoanRepository;
import com.example.library.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository){
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        if(loanRepository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already borrowed");
        }
        return loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return loanRepository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filter, Pageable pageable) {
        return loanRepository.findByBookIsbnOrCustomer(filter.getIsbn(), filter.getCustomer(), pageable);
    }
}
