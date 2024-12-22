package com.example.book.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.book.dto.BookDto;
import com.example.book.entity.Book;
import com.example.book.mapper.BookMapper;
import com.example.book.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;
    
    @Operation(summary = "Get book by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable long id) {
    	Book book = bookService.findById(id);

		BookDto dto = bookMapper.mapToDto(book);
		
		return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get all books")
    @GetMapping("/findall")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @Operation(summary = "Insert a new book")
    @PostMapping("")
    public ResponseEntity<?> insert(@RequestBody @Valid BookDto dto) {
    	Book book = bookMapper.mapToEntity(dto);

		return ResponseEntity.ok(bookService.insert(book));
    }

    @Operation(summary = "Update an existing book")
    @PutMapping("")
    public ResponseEntity<?> update(@RequestBody @Valid BookDto dto) {
    	Book book = bookMapper.mapToEntity(dto);
        return ResponseEntity.ok(bookService.update(book));
    }

    @Operation(summary = "Update book status by ID")
    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Long id, @RequestBody String status) {
        try {
            bookService.updateStatusById(id, status);
            return ResponseEntity.ok("Status updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Delete book by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {
        bookService.deleteById(id);
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Delete books by author ID")
    @DeleteMapping("/auther/{id}")
    public ResponseEntity<?> deleteByAutherId(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.deleteByAutherId(id));
    }
}
