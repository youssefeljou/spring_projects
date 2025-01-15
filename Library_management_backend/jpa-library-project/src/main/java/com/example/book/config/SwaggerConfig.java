package com.example.book.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * Configuration class for setting up Swagger documentation.
 */
@Configuration
public class SwaggerConfig {

	/**
	 * Bean to configure the user management API group.
	 *
	 * @return GroupedOpenApi
	 */
	@Bean
	public GroupedOpenApi userManagementApi() {
		String[] packagesToScan = { "com.service.usermanagement" };
		return GroupedOpenApi.builder().group("User Management API").packagesToScan(packagesToScan)
				.addOperationCustomizer(appTokenHeaderParam()).build();
	}

	/**
	 * Bean to configure the book API group.
	 *
	 * @return GroupedOpenApi
	 */
	@Bean
	public GroupedOpenApi setupApi() {
		String[] packagesToScan = { "com.example.book" };
		return GroupedOpenApi.builder().group("Book API").packagesToScan(packagesToScan)
				.addOperationCustomizer(appTokenHeaderParam()).build();
	}

	/**
	 * Bean to customize operations with an application token header parameter.
	 *
	 * @return OperationCustomizer
	 */
	@Bean
	public OperationCustomizer appTokenHeaderParam() {
		return (Operation operation, HandlerMethod handlerMethod) -> {
			Parameter headerParameter = new Parameter().in(ParameterIn.HEADER.toString()).required(false)
					.schema(new StringSchema()._default("app_token_header_default_value")).name("app_token_header")
					.description("App Token Header");
			operation.addParametersItem(headerParameter);
			return operation;
		};
	}
}
