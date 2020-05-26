package com.alok.spring.batch.mygate.accountreconciler.repository;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    BankTransaction findOneByUtrNo(String utrNo);
}
