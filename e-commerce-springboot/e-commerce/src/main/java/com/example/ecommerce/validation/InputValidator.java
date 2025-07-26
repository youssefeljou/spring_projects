package com.example.ecommerce.validation;

public class InputValidator {
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return username.matches("^[a-z0-9_]+$");
    }


    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 64) {
            return false;
        }

        //make sure that the password contains at least one letter
        boolean hasLetter = password.matches(".*[a-zA-Z].*");

        //make sure that the password contains at least one digit
        boolean hasDigit = password.matches(".*\\d.*");

        //make sure that the password has at least one symbol
        boolean hasSymbol = password.matches(".*[^a-zA-Z0-9].*");

        //check if the password has common patterns and reject if a pattern found
        String lower = password.toLowerCase();
        String[] commonPatterns = {"1234", "abcd", "qwerty", "password", "1111", "0000","admin"};
        for (String pattern : commonPatterns) {
            if (lower.contains(pattern)) {
                return false;
            }
        }

        return hasLetter && hasDigit && hasSymbol;
    }


    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    public static boolean isValidId(Long id) {
        // Example logic to check if the ID is greater than 0
        return id != null && id > 0;
    }

    /***
     * #----UserName validation---#
     * Make sure that the username contains only lowercase letters, numbers and _
     * Make sure that the username doesn't contain Uppercase letters, Spaces,-, "", &, @, #, etc
     *#----Password Validation----#
     * Make sure that the password is strong as follows:
     * Password length is not less than 8 characters
     * Password doesn't contain patterns
     * Should contain at least one number, one symbol, capital, and small letters
     * #----Email Validation----#
     * Make sure that the entered email is in a valid email format
     */
}
