package com.alok.spring.batch.mygate.accountreconciler.configuration;

import com.alok.spring.batch.mygate.accountreconciler.model.BankTransaction;
import com.alok.spring.batch.mygate.accountreconciler.processor.FileArchiveTasklet;
import com.alok.spring.batch.mygate.accountreconciler.utils.FileScanner;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Configuration
public class CommonConfig {
    @Value("${file.input.mygate.dir}")
    private String myGateInputDir;

    @Value("${file.input.mygate.file}")
    private String myGateInputFile;

    @Value("${file.input.hdfc.dir}")
    private String hdfcInputDir;

    @Value("${file.input.hdfc.file}")
    private String hdfcInputFile;

    @Value("${dir.path.reconciled}")
    String outputDirName;

    @Bean
    FileScanner myGateFileScanner() {
        return FileScanner.builder()
                .dirPath(myGateInputDir)
                .fileRegex(myGateInputFile)
                .build();
    }

    @Bean
    FileScanner hdfcFileScanner() {
        return FileScanner.builder()
                .dirPath(hdfcInputDir)
                .fileRegex(hdfcInputFile)
                .build();
    }

    @Bean
    ItemWriter<BankTransaction> myGateReconcileTransactionWriter() {
        String outputFileName = outputDirName + "Bank_Reconciled_" + System.currentTimeMillis() + ".csv";
        Resource csvFile = new FileSystemResource(outputFileName);

        FlatFileItemWriter<BankTransaction> csvTransactionWriter = new FlatFileItemWriter<>();
        csvTransactionWriter.setResource(csvFile);
        csvTransactionWriter.setShouldDeleteIfExists(true);
        csvTransactionWriter.setShouldDeleteIfEmpty(true);
        csvTransactionWriter.setHeaderCallback(writer -> {
            writer.write("Jyothi GT Enclave\n");
            writer.write("Bank Reconciliation for the period 01/04/2020 to 31/03/2021\n");
            writer.write("Opening Balance,0,,Closing Balance,292756.00,,,dd-mm-yyyy\n");
            writer.write("Id,Date,Doc & No,Description,Cheque No,Debit,Credit,Bank Date");
        });
        csvTransactionWriter.setLineAggregator(new DelimitedLineAggregator<BankTransaction>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<BankTransaction>() {
                    {
                        setNames(new String[] {
                                "id","dateAsString","docNo","descriptionWithQuot","chequeNo","debit","credit","bankDateAsString"
                        });
                    }
                });
            }
        });

        return csvTransactionWriter;
    }

    @Bean
    @JobScope
    FileArchiveTasklet fileArchiveTasket(
            @Value("#{jobParameters['FileName']}") String fileName
    ) {
        return FileArchiveTasklet.builder()
                .resource(new FileSystemResource(fileName))
                .build();

    }
}
