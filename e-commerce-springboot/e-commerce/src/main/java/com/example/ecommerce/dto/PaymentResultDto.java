package com.example.ecommerce.dto;

public class PaymentResultDto {
        private String status;
        private String message;

        public PaymentResultDto(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
}
