package com.example.book.config;

import java.util.Arrays;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 */
@Configuration
@OpenAPIDefinition
public class OpenApiConfig {
	private static final String SECURITY_SCHEME_NAME = "Bearer oAuth Token";

	/**
	 * Bean to configure custom OpenAPI documentation.
	 *
	 * @param appDescription Application description
	 * @param appVersion     Application version
	 * @return OpenAPI
	 */
	@Bean
	public OpenAPI customOpenAPI(@Value("${application-description}") String appDescription,
			@Value("${application-version}") String appVersion) {
		return new OpenAPI()
				.info(new Info().title("Sample Application API").version(appVersion).contact(getContact())
						.description(appDescription).termsOfService("http://swagger.io/terms/").license(getLicense()))
				.addSecurityItem(
						new SecurityRequirement().addList(SECURITY_SCHEME_NAME, Arrays.asList("read", "write")))
				.components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
						new SecurityScheme().name(SECURITY_SCHEME_NAME).type(SecurityScheme.Type.HTTP).scheme("bearer")
								.bearerFormat("JWT")));
	}

	/**
	 * Creates a contact object for the API documentation.
	 *
	 * @return Contact
	 */
	private Contact getContact() {
		Contact contact = new Contact();
		contact.setEmail("youssefadel8203@gmail.com");
		contact.setName("Youssef");
		contact.setUrl("https://example.com");
		return contact;
	}

	/**
	 * Creates a license object for the API documentation.
	 *
	 * @return License
	 */
	private License getLicense() {
		License license = new License();
		license.setName("MIT License");
		license.setUrl("https://opensource.org/licenses/MIT");
		return license;
	}
}
