package com.alok.spring.batch.mygate.accountreconciler.processor;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.model.BankAccountTransaction;
import com.alok.spring.batch.mygate.accountreconciler.repository.BankTransactionRepository;
import com.alok.spring.batch.mygate.accountreconciler.repository.BankAccountTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component("myGateAccountProcessor")
public class MyGateAccountProcessor implements ItemProcessor<BankTransaction, BankTransaction> {
    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Autowired
    private BankAccountTransactionRepository bankAccountTransactionRepository;

    @Value("${record.skip.reconciled:true}")
    Boolean skipReconciledRecord;

    @Override
    public BankTransaction process(BankTransaction myGateBankTransaction) {
        if (skipReconciledRecord && myGateBankTransaction.getBankDate() != null) {
            log.info("Skipping already reconciled record - Id: {},\tUTR No: {}", myGateBankTransaction.getId(),myGateBankTransaction.getUtrNo());
            return null;
        }

        if (myGateBankTransaction.getUtrNo() != null && myGateBankTransaction.getUtrNo().length() > 0) {
            BankAccountTransaction bankAccountTransaction = bankAccountTransactionRepository.findOneByUtrNo(myGateBankTransaction.getUtrNo());
            if (bankAccountTransaction != null) {
               if (bankAccountTransaction.getBankDate() != null) {
                   myGateBankTransaction.setBankDate(bankAccountTransaction.getBankDate());
               }
            }
        }

        // save for later bank batch processing will query and get the info - if needed
        if (myGateBankTransaction.getId() != null)
            bankTransactionRepository.save(myGateBankTransaction);

        //if (myGateBankTransaction.getDebit() == 0.0 || myGateBankTransaction.getBankDate() == null) {
        if (myGateBankTransaction.getBankDate() == null) {
            // this will skip the record to go to writer
            return null;
        }

        return myGateBankTransaction;
    }
}
