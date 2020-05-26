package com.alok.spring.batch.mygate.accountreconciler.repository;

import com.alok.spring.batch.mygate.accountreconciler.model.HdfcBankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HdfcBankTransactionRepository extends JpaRepository<HdfcBankTransaction, String> {
    HdfcBankTransaction findOneByUtrNo(String utrNo);
}
