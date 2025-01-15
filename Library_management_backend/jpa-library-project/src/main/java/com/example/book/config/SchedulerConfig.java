package com.example.book.config;

import java.util.concurrent.Executor;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

/**
 * Configuration class for scheduling and asynchronous execution.
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
@EnableAsync
public class SchedulerConfig implements AsyncConfigurer {

	/**
	 * Bean to configure the lock provider using JdbcTemplate.
	 *
	 * @param dataSource DataSource
	 * @return LockProvider
	 */
	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
				.withJdbcTemplate(new JdbcTemplate(dataSource)).usingDbTime().build());
	}

	/**
	 * Bean to configure the thread pool task executor for async tasks.
	 *
	 * @return Executor
	 */
	@Bean(name = "threadPoolTaskExecutor")
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("AsynchThread::");
		executor.initialize();
		return executor;
	}

	/**
	 * Configures the default async executor.
	 *
	 * @return Executor
	 */
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(4);
		taskExecutor.setMaxPoolSize(4);
		taskExecutor.setQueueCapacity(50);
		taskExecutor.initialize();
		return taskExecutor;
	}
}
