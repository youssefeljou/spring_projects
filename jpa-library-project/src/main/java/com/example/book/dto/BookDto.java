package com.example.book.dto;

import org.hibernate.annotations.Formula;

import com.example.book.base.BaseDto;
import com.example.book.entity.Auther;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for the Book entity.
 * This class is used to transfer book data, typically between the API and service layers.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto extends BaseDto<Long> {
	    @Min(value = 5, message = "Price must be at least 5")
	    @Max(value = 50000, message = "Price must be less than 50000")
	    private double price;

	    private AutherDto auther;

	    private String autherName;
	    
	    private String autherEmail;
	    
	    @Transient
	    private double discount;

	    @Formula("(select count(*) from books)")
	    private long bookCount;

	    @PostLoad
	    private void calcDiscount() {
	        this.discount = price * 0.25; // Applying a 25% discount on the book price
	    }
}
