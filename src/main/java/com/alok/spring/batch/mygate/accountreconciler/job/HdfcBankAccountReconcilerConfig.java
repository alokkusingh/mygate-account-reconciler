package com.alok.spring.batch.mygate.accountreconciler.job;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.model.BankAccountTransaction;
import com.alok.spring.batch.mygate.accountreconciler.processor.FileArchiveTasklet;
import com.alok.spring.batch.mygate.accountreconciler.utils.ExcelReader;
import com.alok.spring.batch.mygate.accountreconciler.utils.HdfcRowExtractor;
import com.alok.spring.batch.mygate.accountreconciler.utils.ReconcileReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

@Configuration
@EnableBatchProcessing
@Slf4j
public class HdfcBankAccountReconcilerConfig {

    @Autowired
    private FileArchiveTasklet fileArchiveTasklet;

    @Autowired
    private ReconcileReport reconcileReport;

    @Autowired
    private SkipPolicy skipRecordOnErrorPolicy;

    @Bean("HdfcReconcileTransactionJob")
    public Job hdfcReconcileTransactionJob(JobBuilderFactory jobBuilderFactory,
                                             StepBuilderFactory stepBuilderFactory,
                                             ItemReader<BankAccountTransaction> hdfcExcelReader,
                                             ItemProcessor<BankAccountTransaction, BankTransaction> bankAccountProcessor,
                                             ItemWriter<BankTransaction> myGateReconcileTransactionWriter
    ) {
        Step step0 = stepBuilderFactory.get("HDFC-ETL-file-load")
                .<BankAccountTransaction,BankTransaction>chunk(999)
                .reader(hdfcExcelReader)
                .processor(bankAccountProcessor)
                .writer(myGateReconcileTransactionWriter)
                .faultTolerant().skipPolicy(skipRecordOnErrorPolicy)
                .build();


        Step step1 = stepBuilderFactory.get("HDFC-ETL-file-archive")
                .tasklet(fileArchiveTasklet)
                .build();

        return jobBuilderFactory.get("HDFC-ETL")
                .incrementer(new RunIdIncrementer())
                .start(step0)
                .next(step1)
                .listener(reconcileReport)
                .build();
    }

    @Bean
    @JobScope
    public ExcelReader<BankAccountTransaction> hdfcExcelReader(
            @Value("#{jobParameters['FileName']}") String fileName
    ) {
        log.debug("Initializing hdfcExcelReader");
        log.debug("fileName: {}", fileName);

        ExcelReader<BankAccountTransaction> itemReader = new ExcelReader<>();
        itemReader.setRowExtractor(hdfcRowExtractor());
        itemReader.setResource(new PathResource(fileName));

        return itemReader;
    }

    @Bean
    public HdfcRowExtractor hdfcRowExtractor() {
        return new HdfcRowExtractor();
    }
}
