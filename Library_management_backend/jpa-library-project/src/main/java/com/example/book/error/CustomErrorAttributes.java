package com.example.book.error;

import java.util.Arrays;
import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    // Overriding the method to customize the error attributes
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        
        // Get default error attributes
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        // Customizing the error attributes to include additional info
        errorAttributes.put("locale", webRequest.getLocale().toString()); // Adding locale
        errorAttributes.put("success", Boolean.FALSE); // Setting success to false
        errorAttributes.put("status", errorAttributes.get("error")); // Adding status
        errorAttributes.put("exception", errorAttributes.get("message")); // Adding exception message
        errorAttributes.put("details", Arrays.asList(errorAttributes.get("message"))); // Adding detailed error message
        errorAttributes.remove("error"); // Removing original error key
        errorAttributes.remove("path"); // Removing path key

        return errorAttributes;
    }
}
