package com.example.book.entity;

import org.hibernate.annotations.Formula;

import com.example.book.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing a Book.
 */
@Entity
@Table(name = "books")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BaseEntity<Long> {

	@NotBlank(message = "Name cannot be blank")
    private String name;
	
    @Min(value = 5, message = "Price must be at least 5")
    @Max(value = 50000, message = "Price must be less than 50000")
    private double price;

    @NotNull(message = "Author cannot be null")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
    @ManyToOne
    @JoinColumn(name = "auther_id")
    private Auther auther;

    @Transient
    private double discount;

    @Formula("(select count(*) from books)")
    private long bookCount;

    @PostLoad
    private void calcDiscount() {
        this.discount = price * 0.25; // Applying a 25% discount on the book price
    }
}
