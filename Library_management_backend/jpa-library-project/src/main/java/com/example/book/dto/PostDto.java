package com.example.book.dto;

import java.util.List;

import com.example.book.entity.Book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

	private Long id;
	private String title;
	private String body;
	private Long userId;

	// Getters and setters for each field

	
}
