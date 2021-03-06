package com.alok.spring.batch.mygate.accountreconciler.job;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.processor.FileArchiveTasklet;
import com.alok.spring.batch.mygate.accountreconciler.processor.SkipLineProcessor;
import com.alok.spring.batch.mygate.accountreconciler.repository.HeaderRepository;
import com.alok.spring.batch.mygate.accountreconciler.utils.MyGateFieldSetMapper;
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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;


@Configuration
@EnableBatchProcessing
@Slf4j
public class MyGateAccountReconcilerConfig {
    @Value("${fields.name.mygate:#{null}}")
    private String[] fieldNames;

    @Autowired
    private MyGateFieldSetMapper myGateFieldSetMapper;

    @Autowired
    private FileArchiveTasklet fileArchiveTasklet;

    @Autowired
    private SkipLineProcessor skipLineProcessor;

    @Autowired
    private HeaderRepository headerRepository;

    @Autowired
    private ReconcileReport reconcileReport;

    @Autowired
    private SkipPolicy skipRecordOnErrorPolicy;

    @Bean("MyGateReconcileTransactionJob")
    public Job myGateReconcileTransactionJob(JobBuilderFactory jobBuilderFactory,
                                          StepBuilderFactory stepBuilderFactory,
                                          ItemReader<BankTransaction> myGateAccountItemReader,
                                          ItemProcessor<BankTransaction, BankTransaction> myGateAccountProcessor,
                                          ItemWriter<BankTransaction> myGateReconcileTransactionWriter
    ) {
        Step step0 = stepBuilderFactory.get("MyGate-ETL-file-load")
                .<BankTransaction,BankTransaction>chunk(999)
                .reader(myGateAccountItemReader)
                .processor(myGateAccountProcessor)
                .writer(myGateReconcileTransactionWriter)
                .faultTolerant().skipPolicy(skipRecordOnErrorPolicy)
                .build();

        Step step1 = stepBuilderFactory.get("MyGate-ETL-file-archive")
                .tasklet(fileArchiveTasklet)
                .build();

        return jobBuilderFactory.get("MyGate-ETL")
                .incrementer(new RunIdIncrementer())
                .start(step0)
                .next(step1)
                .listener(reconcileReport)
                .build();
    }

    @Bean
    @JobScope
    public FlatFileItemReader<BankTransaction> myGateAccountItemReader(
            @Value("#{jobParameters['FileName']}") String fileName
    ) {

        headerRepository.deleteAll();
        FlatFileItemReader<BankTransaction> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setName("MyGate-CSV-Reader");
        flatFileItemReader.setResource(new FileSystemResource(fileName));
        flatFileItemReader.setLineMapper(mygateTransactionLineMapper());
        flatFileItemReader.setStrict(false);
        flatFileItemReader.setLinesToSkip(4);
        flatFileItemReader.setSkippedLinesCallback(skipLineProcessor);
        flatFileItemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy() {
            @Override
            public boolean isEndOfRecord(String line) {
                return line == null
                        || "Jyothi GT Enclave".equals(line)
                        || line.startsWith("Bank Reconciliation")
                        || line.startsWith("Opening Balance")
                        || line.startsWith("Id,Date,Doc")
                        || line.contains("Txn Id:")
                        //|| line.contains("Payment")
                        //|| line.contains("Payment Voucher")
                        || line.contains(",None,")
                        || line.contains("Ref: ")
                        ;
            }

            @Override
            public String postProcess(String record) {
                return record;
            }

            @Override
            public String preProcess(String record) {
                return record;
            }
        });

        return flatFileItemReader;
    }

    @Bean
    @JobScope
    public LineMapper<BankTransaction> mygateTransactionLineMapper() {
        DefaultLineMapper<BankTransaction> defaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(fieldNames);


        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(myGateFieldSetMapper);

        return defaultLineMapper;
    }
}
