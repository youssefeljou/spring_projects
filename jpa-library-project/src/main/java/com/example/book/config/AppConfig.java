package com.example.book.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class to set up application-level beans. Enables AspectJ
 * support for AOP (Aspect-Oriented Programming).
 */
@EnableAspectJAutoProxy
@Configuration
public class AppConfig {

	/**
	 * Bean to configure the RestTemplate, which is used for making HTTP requests.
	 * This RestTemplate can be injected wherever needed to perform RESTful
	 * operations.
	 *
	 * @return a new instance of RestTemplate
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
