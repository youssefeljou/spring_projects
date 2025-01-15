package com.example.book.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class used for searching authors and their books based on multiple criteria.
 * This class is used to encapsulate the search parameters for filtering authors
 * and books.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AutherSearch {

	private String autherName; // The name of the author to search for
	private String email; // The author's email to search for
	private String ipAddress; // The author's IP address to search for
	private String bookName; // The book's name to search for
	private Double price; // The price range of the book to search for

	// Getter and setter methods for all fields

	
}
