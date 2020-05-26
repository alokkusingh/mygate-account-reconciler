package com.alok.spring.batch.mygate.accountreconciler.utils;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyGateFieldSetMapper implements FieldSetMapper<BankTransaction> {

    @Autowired
    private DefaultFieldExtractor txnIdExtractor;

    @Autowired
    private DefaultFieldExtractor utrNoExtractor;

    @Override
    public BankTransaction mapFieldSet(FieldSet fieldSet) {
        BankTransaction transaction = new BankTransaction();
        transaction.setId(fieldSet.readLong("id"));
        transaction.setDate(fieldSet.readDate("date"));
        transaction.setDocNo(fieldSet.readString("docNo"));
        transaction.setDescription(fieldSet.readString("description"));
        transaction.setChequeNo(fieldSet.readString("chequeNo"));
        transaction.setDebit(fieldSet.readDouble("debit"));
        transaction.setCredit(fieldSet.readDouble("credit"));
        String strBankDate = fieldSet.readString("bankDate");
        if (strBankDate.length() > 0) {
            transaction.setBankDate(fieldSet.readDate("bankDate"));
        }

        enrichTransaction(transaction);

        log.debug("Mapped transaction {}", transaction);

        return transaction;
    }

    private void enrichTransaction(BankTransaction transaction) {
        String description = transaction.getDescription();
        transaction.setTxnId(txnIdExtractor.getField(description));
        transaction.setUtrNo(utrNoExtractor.getField(description));
    }

    @Bean
    public DefaultFieldExtractor txnIdExtractor() {
        DefaultFieldExtractor fieldExtractor = new DefaultFieldExtractor();
        fieldExtractor.setStringPatterns(
                new String[] {
                        // replace comma from pattern to empty
                        "(?<=Txn Id: )\\w+",
                }
        );
        return fieldExtractor;
    }

    @Bean
    public DefaultFieldExtractor utrNoExtractor() {
        DefaultFieldExtractor fieldExtractor = new DefaultFieldExtractor();
        fieldExtractor.setStringPatterns(
                new String[] {
                        // replace comma from pattern to empty
                        "(?<=UTR NO.)\\w+",
                }
        );
        return fieldExtractor;
    }
}
