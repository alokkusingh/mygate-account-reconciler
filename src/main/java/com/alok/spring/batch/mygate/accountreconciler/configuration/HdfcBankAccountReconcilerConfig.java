package com.alok.spring.batch.mygate.accountreconciler.configuration;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.model.HdfcBankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.processor.FileArchiveTasklet;
import com.alok.spring.batch.mygate.accountreconciler.utils.ExcelReader;
import com.alok.spring.batch.mygate.accountreconciler.utils.HdfcRowExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
    @Value("${fields.name.bank.hdfc:#{null}}")
    private String[] fieldNames;

    @Value("${file.path.reconciled}")
    String outputFileName;

    @Autowired
    private FileArchiveTasklet fileArchiveTasklet;

    @Bean("HdfcReconcileTransactionJob")
    public Job hdfcReconcileTransactionJob(JobBuilderFactory jobBuilderFactory,
                                             StepBuilderFactory stepBuilderFactory,
                                             ItemReader<HdfcBankTransaction> hdfcExcelReader,
                                             ItemProcessor<HdfcBankTransaction, BankTransaction> hdfcAccountProcessor,
                                             ItemWriter<BankTransaction> myGateReconcileTransactionWriter
    ) {
        Step step0 = stepBuilderFactory.get("HDFC-ETL-file-load")
                .<HdfcBankTransaction,BankTransaction>chunk(999)
                .reader(hdfcExcelReader)
                .processor(hdfcAccountProcessor)
                .writer(myGateReconcileTransactionWriter)
                .build();


        Step step1 = stepBuilderFactory.get("HDFC-ETL-file-archive")
                .tasklet(fileArchiveTasklet)
                .build();

        return jobBuilderFactory.get("HDFC-ETL")
                .incrementer(new RunIdIncrementer())
                .start(step0)
                .next(step1)
                .build();
    }

    @Bean
    @JobScope
    public ExcelReader<HdfcBankTransaction> hdfcExcelReader(
            @Value("#{jobParameters['FileName']}") String fileName
    ) {
        log.debug("Initializing hdfcExcelReader");
        log.debug("fileName: {}", fileName);

        ExcelReader<HdfcBankTransaction> itemReader = new ExcelReader<>();
        itemReader.setRowExtractor(hdfcRowExtractor());
        itemReader.setResource(new PathResource(fileName));

        return itemReader;
    }

    @Bean
    public HdfcRowExtractor hdfcRowExtractor() {
        HdfcRowExtractor hdfcRowExtractor = new HdfcRowExtractor();
        return hdfcRowExtractor;
    }
}
