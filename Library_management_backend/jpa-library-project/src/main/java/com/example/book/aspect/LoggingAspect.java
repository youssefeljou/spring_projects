package com.example.book.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
public class LoggingAspect {

	// Logger log = LoggerFactory.getLogger(LoggingAspect.class);

	// Pointcut for repository layer methods
	@Pointcut(value = "execution(* com.example.book.repository.*.*(..))")
	public void forRepositoryLog() {
	}

	// Pointcut for service layer methods
	@Pointcut(value = "execution(* com.example.book.service.*.*(..))")
	public void forServiceLog() {
	}

	// Pointcut for controller layer methods
	@Pointcut(value = "execution(* com.example.book.controller.*.*(..))")
	public void forControllerLog() {
	}

	// Pointcut that combines repository, service, and controller layers
	@Pointcut(value = "forRepositoryLog() || forServiceLog() || forControllerLog()")
	public void forAllApp() {
	}

	// Advice that runs before methods matched by forAllApp() pointcut
	@Before(value = "forAllApp()")
	public void beforeMethod(JoinPoint joinPoint) {
		// Log method name
		String methodName = joinPoint.getSignature().toShortString();
		log.info("====> Method Name is >> {}", methodName);

		// Log method arguments
		Object[] args = joinPoint.getArgs();
		for (Object arg : args) {
			log.info("===> argument >> {}", arg);
		}
	}
}
