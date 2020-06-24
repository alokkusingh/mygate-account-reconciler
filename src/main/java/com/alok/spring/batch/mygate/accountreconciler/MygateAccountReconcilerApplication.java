package com.alok.spring.batch.mygate.accountreconciler;

import com.alok.spring.batch.mygate.accountreconciler.annotation.LogExecutionTime;
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
	private FileScanner idfcFileScanner;

	@Autowired
	@Qualifier("MyGateReconcileTransactionJob")
	private Job myGateReconcileTransactionJob;

	@Autowired
	@Qualifier("HdfcReconcileTransactionJob")
	private Job hdfcReconcileTransactionJob;

	@Autowired
	@Qualifier("IdfcReconcileTransactionJob")
	private Job idfcReconcileTransactionJob;

	private JobExecution myGateJobExecution;
	private JobExecution hdfcJobExecution;

	private final Object mutex = new Object();


	public static void main(String[] args) {
		SpringApplication.run(MygateAccountReconcilerApplication.class, args);
	}


	@LogExecutionTime
	@Scheduled(cron = "0 * * * * ?")
	public void performMyGateBankTransactionLoad() throws Exception
	{
		List<String> files = myGateFileScanner.getFiles();
		log.info("Got files: {}", files);
		for (String file: files) {
			JobParameters params = new JobParametersBuilder()
					.addString("JobName", "MyGateTransactionReconciliationJob")
					.addString("JobID", String.valueOf(System.currentTimeMillis()))
					.addString("FileName", file)
					.toJobParameters();

			synchronized (mutex) {
				myGateJobExecution = jobLauncher.run(myGateReconcileTransactionJob, params);
			}
		}
	}

	@LogExecutionTime
	@Scheduled(cron = "20 * * * * ?")
	public void performHdfcBankTransactionLoad() throws Exception
	{
		List<String> files = hdfcFileScanner.getFiles();
		log.info("Got files: {}", files);
		for (String file: files) {
			JobParameters params = new JobParametersBuilder()
					.addString("JobName", "HdfcTransactionReconciliationJob")
					.addString("JobID", String.valueOf(System.currentTimeMillis()))
					.addString("FileName", file)
					.toJobParameters();

			synchronized (mutex) {
				hdfcJobExecution = jobLauncher.run(hdfcReconcileTransactionJob, params);
			}
		}
	}

	@LogExecutionTime
	@Scheduled(cron = "40 * * * * ?")
	public void performIdfcBankTransactionLoad() throws Exception
	{
		List<String> files = idfcFileScanner.getFiles();
		log.info("Got files: {}", files);
		for (String file: files) {
			JobParameters params = new JobParametersBuilder()
					.addString("JobName", "IdfcTransactionReconciliationJob")
					.addString("JobID", String.valueOf(System.currentTimeMillis()))
					.addString("FileName", file)
					.toJobParameters();

			synchronized (mutex) {
				hdfcJobExecution = jobLauncher.run(idfcReconcileTransactionJob, params);
			}
		}
	}
}
