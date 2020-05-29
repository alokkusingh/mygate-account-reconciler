package com.alok.spring.batch.mygate.accountreconciler.repository;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    BankTransaction findOneByUtrNo(String utrNo);
    List<BankTransaction> findAllByBankDate(Date bankDate);
}
