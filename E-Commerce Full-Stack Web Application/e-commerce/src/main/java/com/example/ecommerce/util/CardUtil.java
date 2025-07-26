package com.example.ecommerce.util;

import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;

public final class CardUtil {
    private CardUtil() {
    }

    public static boolean passesLuhn(String pan) {
        return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(pan);
    }

    public static String brand(String pan) {
        if (pan.startsWith("4")) return "VISA";
        if (pan.matches("^5[1-5].*")) return "MASTERCARD";
        if (pan.matches("^3[47].*")) return "AMEX";
        if (pan.matches("^6(?:011|5).*")) return "DISCOVER";
        return "UNKNOWN";
    }

    /**
     * generate token based on the card type
     * @param brand
     * @return
     */
    public static String successToken(String brand) {
        return switch (brand) {
            case "VISA" -> "tok_visa";
            case "MASTERCARD" -> "tok_mastercard";
            case "AMEX" -> "tok_amex";
            case "DISCOVER" -> "tok_discover";
            default -> "tok_chargeDeclined";
        };
    }

    public static String failureToken() {
        return "tok_chargeDeclined";
    }
}