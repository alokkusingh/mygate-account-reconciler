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
        try {
            log.debug("Raw record: {}", fieldSet.toString());
            BankTransaction transaction = BankTransaction.builder()
                    .id(fieldSet.readLong("id"))
                    .date(fieldSet.readDate("date"))
                    .docNo(fieldSet.readString("docNo"))
                    .description(fieldSet.readString("description"))
                    .chequeNo(fieldSet.readString("chequeNo"))
                    .debit(fieldSet.readDouble("debit"))
                    .credit(fieldSet.readDouble("credit"))
                    .bankDate(fieldSet.readString("bankDate").length() > 0 ? fieldSet.readDate("bankDate") : null)
                    .build();

            enrichTransaction(transaction);

            log.debug("Mapped transaction {}", transaction);

            return transaction;
        } catch (Exception e) {
            log.error("Error parsing record: {}", fieldSet.toString());
            log.error(e.getMessage(), e);
        }
        return new BankTransaction();
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
                        "(?<=Ref: )\\w+",
                }
        );
        return fieldExtractor;
    }
}
