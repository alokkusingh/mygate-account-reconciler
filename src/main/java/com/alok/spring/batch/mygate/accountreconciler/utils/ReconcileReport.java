package com.alok.spring.batch.mygate.accountreconciler.utils;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.repository.BankTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ReconcileReport implements JobExecutionListener {
    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("{} - started processing {}",jobExecution.getJobParameters().getString("JobName"),
                jobExecution.getJobParameters().getString("FileName"));
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("{} - completed!",jobExecution.getJobParameters().getString("JobName"));
        log.info("{}", prepareReport());
    }

    private String prepareReport() {
       StringBuilder reportString = new StringBuilder("\n-------------------------------------- RECONCILE REPORT --------------------------------------\n\n");

        List<BankTransaction> unreconciledBankTransactions = bankTransactionRepository.findAllByBankDate(null);
        int recordCount = bankTransactionRepository.findAll().size();

        if (recordCount == 0) {
            reportString.append("\tNo Record Reconciled!\n\n");
        } else {
                reportString.append(String.format("\t%d/%d Record(s) Reconciled!\n\n", recordCount - unreconciledBankTransactions.size(), recordCount));
            if (recordCount != unreconciledBankTransactions.size()) {
                reportString.append("\tUnreconciled Records:\n");
                reportString.append(String.format("\t%10s%20s%10s%10s%35s\n", "Id", "UTR No", "Credit", "Debit", "Doc No"));
                for (BankTransaction bankTransaction : unreconciledBankTransactions) {
                    reportString.append(String.format("\t%10s%20s%10s%10s%35s\n", bankTransaction.getId(), bankTransaction.getUtrNo(), bankTransaction.getCredit(), bankTransaction.getDebit(), bankTransaction.getDocNo()));
                }
            }
        }
        reportString.append("----------------------------------------------------------------------------------------------\n");

        return reportString.toString();
    }
}
