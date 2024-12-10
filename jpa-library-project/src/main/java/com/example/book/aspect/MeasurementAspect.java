package com.example.book.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.book.service.AutherService;

import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
public class MeasurementAspect {

	// Logger log = LoggerFactory.getLogger(MeasurementAspect.class);

	// Advice that measures the execution time of methods in PostController
	@Around("execution(* com.example.book.controller.PostController.*(..))")
	public Object logTime(ProceedingJoinPoint joinPoint) throws Throwable {
		// Record start time
		Long startTime = System.currentTimeMillis();

		// Build initial log message
		StringBuilder sb = new StringBuilder("KPI: ");
		sb.append("{").append(joinPoint.getKind()).append("} \t for: ").append(joinPoint.getSignature())
				.append("\t withArgs: ").append("(")
				.append(StringUtils.arrayToDelimitedString(joinPoint.getArgs(), ",")).append(")");
		sb.append("\t took: ");

		// Proceed with method execution
		Object returnValue = joinPoint.proceed();

		// Log execution time
		log.info(sb.append(System.currentTimeMillis() - startTime).append(" ms.").toString());

		// Return the result
		return returnValue;
	}
}
