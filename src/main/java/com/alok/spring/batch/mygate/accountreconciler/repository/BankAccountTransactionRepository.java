package com.alok.spring.batch.mygate.accountreconciler.repository;

import com.alok.spring.batch.mygate.accountreconciler.model.BankAccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountTransactionRepository extends JpaRepository<BankAccountTransaction, String> {
    BankAccountTransaction findOneByUtrNo(String utrNo);
}
