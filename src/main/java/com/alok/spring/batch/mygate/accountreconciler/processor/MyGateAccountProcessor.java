package com.alok.spring.batch.mygate.accountreconciler.processor;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.model.HdfcBankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.repository.BankTransactionRepository;
import com.alok.spring.batch.mygate.accountreconciler.repository.HdfcBankTransactionRepository;
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
    private HdfcBankTransactionRepository hdfcBankTransactionRepository;

    @Value("${record.skip.reconciled:true}")
    Boolean skipReconciledRecord;

    @Override
    public BankTransaction process(BankTransaction myGateBankTransaction) {
        if (skipReconciledRecord && myGateBankTransaction.getBankDate() != null) {
            log.info("Skipping already reconciled record -\tId: {},\tUTR No: {}", myGateBankTransaction.getId(),myGateBankTransaction.getUtrNo());
            return null;
        }

        if (myGateBankTransaction.getUtrNo() != null && myGateBankTransaction.getUtrNo().length() > 0) {
            HdfcBankTransaction hdfcBankTransaction = hdfcBankTransactionRepository.findOneByUtrNo(myGateBankTransaction.getUtrNo());
            if (hdfcBankTransaction != null) {
               if (hdfcBankTransaction.getBankDate() != null) {
                   myGateBankTransaction.setBankDate(hdfcBankTransaction.getBankDate());
               }
            }
        }

        // save for later bank batch processing will query and get the info - if needed
        bankTransactionRepository.save(myGateBankTransaction);

        if (myGateBankTransaction.getDebit() == 0.0 || myGateBankTransaction.getBankDate() == null) {
            // this will skip the record to go to writer
            return null;
        }

        return myGateBankTransaction;
    }
}
