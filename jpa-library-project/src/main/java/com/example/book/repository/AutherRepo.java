package com.example.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.book.base.BaseRepository;
import com.example.book.entity.Auther;

/**
 * Repository interface for {@link Auther} entity. Extends
 * {@link BaseRepository} and {@link JpaSpecificationExecutor} to provide CRUD
 * operations and specification execution.
 */
@Repository
public interface AutherRepo extends BaseRepository<Auther, Long>, JpaSpecificationExecutor<Auther> {

	/**
	 * Find an author by their email.
	 *
	 * @param email the email of the author
	 * @return an optional containing the author if found, otherwise empty
	 */
	Optional<Auther> findByEmail(String email);

	/**
	 * Retrieve all authors with their associated books loaded eagerly using the
	 * {@link EntityGraph}. This method uses {@link EntityGraph} to fetch the
	 * related "books" attribute along with the author in a single query, improving
	 * performance by avoiding lazy loading.
	 *
	 * @return a list of authors, with their associated books
	 */
	@Override
	@EntityGraph(attributePaths = "books") // Specifies eager loading for the
	// "books" attribute of Author
	List<Auther> findAll();

	/**
	 * Retrieve an author by their ID, with the associated books loaded eagerly
	 * using the {@link EntityGraph}. This method fetches the author along with
	 * their books in a single query to avoid lazy loading issues.
	 *
	 * @param id the ID of the author
	 * @return an optional containing the author if found, otherwise empty
	 */
	@Override
	@EntityGraph(attributePaths = "books") // Specifies eager loading for the
	// "books" attribute of Author
	Optional<Auther> findById(Long id);
}
