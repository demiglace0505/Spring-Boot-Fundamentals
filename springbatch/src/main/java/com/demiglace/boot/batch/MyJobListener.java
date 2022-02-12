package com.demiglace.boot.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class MyJobListener implements JobExecutionListener {
	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("JOB STARTED");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("JOB ENDED " + jobExecution.getStatus().toString());
	}
}
