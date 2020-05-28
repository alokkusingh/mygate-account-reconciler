package com.alok.spring.batch.mygate.accountreconciler;

import com.alok.spring.batch.mygate.accountreconciler.utils.FileScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class MygateAccountReconcilerApplication {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private FileScanner myGateFileScanner;

	@Autowired
	private FileScanner hdfcFileScanner;

	@Autowired
	@Qualifier("MyGateReconcileTransactionJob")
	private Job myGateReconcileTransactionJob;

	@Autowired
	@Qualifier("HdfcReconcileTransactionJob")
	private Job hdfcReconcileTransactionJob;

	private JobExecution myGateJobExecution;
	private JobExecution hdfcJobExecution;

	private final Object mutex = new Object();


	public static void main(String[] args) {
		SpringApplication.run(MygateAccountReconcilerApplication.class, args);
	}


	@Scheduled(cron = "1 * * * * ?")
	public void performMyGateBankTransactionLoad() throws Exception
	{
		List<String> files = myGateFileScanner.getFiles();
		log.info("Got files: {}", files);
		for (String file: files) {
			JobParameters params = new JobParametersBuilder()
					.addString("JobID", String.valueOf(System.currentTimeMillis()))
					.addString("FileName", file)
					.toJobParameters();

			log.info("Job performMyGateBankTransactionLoad waiting to start processing: {}", file);
			synchronized (mutex) {
				if (myGateJobExecution != null)
					myGateJobExecution.stop();
				log.info("Job performMyGateBankTransactionLoad started processing: {}", file);
				myGateJobExecution = jobLauncher.run(myGateReconcileTransactionJob, params);
			}
			log.info("Job performMyGateBankTransactionLoad completed processing: {}", file);
		}
	}

	@Scheduled(cron = "30 * * * * ?")
	public void performHdfcBankTransactionLoad() throws Exception
	{
		List<String> files = hdfcFileScanner.getFiles();
		log.info("Got files: {}", files);
		for (String file: files) {
			log.info("Job performMyGateBankTransactionLoad waiting to start processing: {}", file);
			JobParameters params = new JobParametersBuilder()
					.addString("JobID", String.valueOf(System.currentTimeMillis()))
					.addString("FileName", file)
					.toJobParameters();

			synchronized (mutex) {
				if (hdfcJobExecution != null)
					hdfcJobExecution.stop();
				log.info("Job performMyGateBankTransactionLoad started processing: {}", file);
				hdfcJobExecution = jobLauncher.run(hdfcReconcileTransactionJob, params);
			}
			log.info("Job performMyGateBankTransactionLoad completed processing: {}", file);
		}
	}
}
