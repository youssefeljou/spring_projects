package com.example.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.book.base.BaseRepository;
import com.example.book.entity.Book;

/**
 * Repository interface for {@link Book} entity. Extends {@link BaseRepository}
 * to provide CRUD operations.
 */
@Repository
public interface BookRepo extends BaseRepository<Book, Long> {

	/**
	 * Find a book by its ID with author details.
	 *
	 * @param id the ID of the book
	 * @return an optional containing the book if found, otherwise empty
	 */
	@Override
	@EntityGraph(attributePaths = "auther")
	Optional<Book> findById(Long id);

	/**
	 * Find all books with author details.
	 *
	 * @return a list of all books
	 */
	@Override
	@EntityGraph(attributePaths = { "auther" })
	List<Book> findAll();

	/**
	 * Delete books by author ID.
	 *
	 * @param id the ID of the author
	 * @return the number of books deleted
	 */
	@Transactional
	@Modifying
	@Query("delete from Book where auther.id = :id")
	int deleteByAutherId(Long id);
}
