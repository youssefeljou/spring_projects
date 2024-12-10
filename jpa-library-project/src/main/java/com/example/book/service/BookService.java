package com.example.book.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.book.base.BaseService;
import com.example.book.entity.Book;
import com.example.book.repository.BookRepo;

import lombok.RequiredArgsConstructor;

/**
 * Service class for managing books.
 */
@Service
@RequiredArgsConstructor
//@Log4j2
public class BookService extends BaseService<Book, Long> {

    private final BookRepo bookRepo;

    /*
    //@Autowired or make it final and make @RequiredArgsConstructor
    public BookService(BookRepo bookRepo) {
        super();
        this.bookRepo = bookRepo;
    }
*/
    
    /**
     * Updates an existing book.
     * @param entity The book to update.
     * @return The updated book.
     */
    public Book update(Book entity) {
        Book book = findById(entity.getId());
        book.setName(entity.getName());
        return super.update(entity);
    }

    /**
     * Deletes books by author ID.
     * @param id The ID of the author whose books to delete.
     * @return The number of deleted books.
     */
    public int deleteByAutherId(Long id) {
        return bookRepo.deleteByAutherId(id);
    }
}
