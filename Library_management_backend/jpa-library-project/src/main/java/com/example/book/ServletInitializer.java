package com.example.book;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Servlet initializer class for deploying the application in a servlet container.
 * This class configures the application when it's deployed as a WAR file.
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * Configures the application.
     * 
     * @param application A builder for the application context
     * @return A configured SpringApplicationBuilder instance
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JpaLibraryProject1Application.class);
    }
}
