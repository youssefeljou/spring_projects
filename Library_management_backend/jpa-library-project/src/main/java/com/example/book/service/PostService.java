package com.example.book.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.book.dto.PostDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	// Base URL for the JSONPlaceholder posts API
	private static final String BASE_POST_URL = "https://jsonplaceholder.typicode.com/posts";

	//@Autowired or make it final and make @RequiredArgsConstructor
	private final RestTemplate restTemplate;
	
	/**
	 * Retrieves a post by its ID.
	 * 
	 * @param id The ID of the post to retrieve.
	 * @return The post details.
	 */
	public PostDto getPostById(Long id) {
		//RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<PostDto> result = restTemplate.getForEntity(BASE_POST_URL + "/" + id, PostDto.class);
		return result.getBody();
	}

	/**
	 * Retrieves all posts.
	 * 
	 * @return A list of all posts.
	 */
	public List<PostDto> getAllPost() {
		
		ResponseEntity<List> result = restTemplate.getForEntity(BASE_POST_URL, List.class);
		return result.getBody();
	}

	/**
	 * Adds a new post.
	 * 
	 * @param dto The post details to add.
	 * @return The added post details.
	 */
	public PostDto addPost(PostDto dto) {
		//RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("accept", "application/json");
		headers.add("accept-language", "en");

		HttpEntity<PostDto> request = new HttpEntity<>(dto);

		ResponseEntity<PostDto> result = restTemplate.exchange(BASE_POST_URL, HttpMethod.POST, request, PostDto.class);

		return result.getBody();
	}

	/**
	 * Updates an existing post.
	 * 
	 * @param dto The post details to update.
	 */
	public void updatePost(PostDto dto) {
		//RestTemplate restTemplate = new RestTemplate();

		HttpEntity<PostDto> request = new HttpEntity<>(dto);

		restTemplate.put(BASE_POST_URL, request);
	}

	/**
	 * Deletes a post by its ID.
	 * 
	 * @param id The ID of the post to delete.
	 */
	public void deletePostById(Long id) {
		//RestTemplate restTemplate = new RestTemplate();

		restTemplate.delete(BASE_POST_URL + "/" + id);
	}
}
