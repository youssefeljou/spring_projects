package com.example.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.dto.PostDto;
import com.example.book.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

	//@Autowired or make it final and make @RequiredArgsConstructor
	private final PostService postService;

	/**
	 * Endpoint to get a post by ID.
	 * 
	 * @param id The ID of the post.
	 * @return The post details.
	 */
	@Operation(summary = "Get post by ID", description = "Retrieve a single post by its ID.")
	@GetMapping("/{id}")
	public ResponseEntity<?> getPostById(@PathVariable Long id) {
		return ResponseEntity.ok(postService.getPostById(id));
	}

	/**
	 * Endpoint to get all posts.
	 * 
	 * @return A list of all posts.
	 */
	@Operation(summary = "Get all posts", description = "Retrieve a list of all posts.")
	@GetMapping("")
	public ResponseEntity<?> getAllPost() {
		return ResponseEntity.ok(postService.getAllPost());
	}

	/**
	 * Endpoint to add a new post.
	 * 
	 * @param dto The post details to add.
	 * @return The added post details.
	 */
	@Operation(summary = "Add a new post", description = "Create a new post with the provided details.")
	@PostMapping("")
	public ResponseEntity<?> addPost(@RequestBody PostDto dto) {
		return ResponseEntity.ok(postService.addPost(dto));
	}
}
