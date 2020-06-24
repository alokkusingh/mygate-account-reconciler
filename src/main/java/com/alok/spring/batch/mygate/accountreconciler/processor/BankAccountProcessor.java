package com.alok.spring.batch.mygate.accountreconciler.processor;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.model.BankAccountTransaction;
import com.alok.spring.batch.mygate.accountreconciler.repository.BankTransactionRepository;
import com.alok.spring.batch.mygate.accountreconciler.repository.BankAccountTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component("bankAccountProcessor")
public class BankAccountProcessor implements ItemProcessor<BankAccountTransaction, BankTransaction> {
    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Autowired
    private BankAccountTransactionRepository bankAccountTransactionRepository;

    @Override
    public BankTransaction process(BankAccountTransaction bankTransaction) {
        log.debug("bankAccountTransaction: {}", bankTransaction);
        BankTransaction myGateBankTransaction = null;
        if (bankTransaction.getUtrNo() != null) {
            myGateBankTransaction = bankTransactionRepository.findOneByUtrNo(bankTransaction.getUtrNo());
            if (myGateBankTransaction != null) {
               myGateBankTransaction.setBankDate(bankTransaction.getBankDate());
            }
        }

        // save for later MyGate batch processing will query and get the info - if needed
        bankAccountTransactionRepository.save(bankTransaction);

        if (myGateBankTransaction == null || myGateBankTransaction.getDebit() == 0.0) {
            // this will skip the record to go to writer
            return null;
        }

        return myGateBankTransaction;
    }
}
