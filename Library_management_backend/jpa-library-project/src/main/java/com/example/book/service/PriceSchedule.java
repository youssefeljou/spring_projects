package com.example.book.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.SchedulerLock;

/**
 * This class handles scheduled tasks for computing book prices and discounts.
 */
@Component
@Log4j2
public class PriceSchedule {

	// Logger instance for logging messages
	// Logger log = LoggerFactory.getLogger(PriceSchedule.class);

	/**
	 * Scheduled task to compute book prices. Runs at a fixed rate of 200000
	 * milliseconds (200 seconds). Uses @SchedulerLock to ensure that only one
	 * instance of this task runs at a time across the cluster. Uses @Async to run
	 * this task asynchronously.
	 * 
	 * @throws InterruptedException If the thread is interrupted while sleeping.
	 */
	@Scheduled(fixedRate = 200000)
	@SchedulerLock(name = "bookComputePrice")
	@Async
	public void computePrice() throws InterruptedException {
		// Simulate a delay of 4 seconds
		Thread.sleep(4000);

		// Log the current date and time
		log.info("compute price >> " + LocalDateTime.now());
	}

	/**
	 * Scheduled task to compute book discounts. Runs at a fixed rate of 200000
	 * milliseconds (200 seconds). Uses @SchedulerLock to ensure that only one
	 * instance of this task runs at a time across the cluster. Uses @Async to run
	 * this task asynchronously.
	 * 
	 * @throws InterruptedException If the thread is interrupted while sleeping.
	 */
	@Scheduled(fixedRate = 200000)
	@SchedulerLock(name = "bookComputeDiscount")
	@Async
	public void computeDiscount() throws InterruptedException {
		// Simulate a delay of 4 seconds
		Thread.sleep(4000);

		// Log the current date and time
		log.info("compute discount >> " + LocalDateTime.now());
	}
}
