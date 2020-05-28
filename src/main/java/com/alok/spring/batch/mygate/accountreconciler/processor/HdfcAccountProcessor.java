package com.alok.spring.batch.mygate.accountreconciler.processor;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.model.HdfcBankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.repository.BankTransactionRepository;
import com.alok.spring.batch.mygate.accountreconciler.repository.HdfcBankTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component("hdfcAccountProcessor")
public class HdfcAccountProcessor implements ItemProcessor<HdfcBankTransaction, BankTransaction> {
    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Autowired
    private HdfcBankTransactionRepository hdfcBankTransactionRepository;

    @Override
    public BankTransaction process(HdfcBankTransaction hdfcTransaction) {
        log.debug("hdfcTransaction: {}", hdfcTransaction);
        BankTransaction myGateBankTransaction = null;
        if (hdfcTransaction.getUtrNo() != null) {
            myGateBankTransaction = bankTransactionRepository.findOneByUtrNo(hdfcTransaction.getUtrNo());
            if (myGateBankTransaction != null) {
               myGateBankTransaction.setBankDate(hdfcTransaction.getBankDate());
            }
        }

        // save for later MyGate batch processing will query and get the info - if needed
        hdfcBankTransactionRepository.save(hdfcTransaction);

        if (myGateBankTransaction == null || myGateBankTransaction.getDebit() == 0.0) {
            // this will skip the record to go to writer
            return null;
        }

        return myGateBankTransaction;
    }
}
