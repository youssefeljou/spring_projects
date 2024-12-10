package com.example.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.book.service.FileUploadService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileUploadController {

	// Service to handle file upload operations
	// @Autowired or make it final and make @RequiredArgsConstructor
	private final FileUploadService fileUploadService;

	/**
	 * Endpoint to upload a file.
	 * 
	 * @param id       the ID associated with the file
	 * @param pathType the type of path where the file will be stored
	 * @param file     the file to be uploaded
	 * @return ResponseEntity with the uploaded file's name
	 */
	@Operation(summary = "Upload a file")
	@PostMapping("/upload")
	public ResponseEntity<Object> uploadFile(@RequestParam Long id, @RequestParam String pathType,
			@RequestParam MultipartFile file) {
		// Convert and store the uploaded file
		String fileName = fileUploadService.storeFile(fileUploadService.convertMultiPartFileToFile(file), id, pathType);

		// Return the file name in the response
		return ResponseEntity.ok(fileName);
	}
}
