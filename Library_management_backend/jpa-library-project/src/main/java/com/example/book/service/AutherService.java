package com.example.book.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.book.base.BaseService;
import com.example.book.entity.Auther;
import com.example.book.entity.AutherSearch;
import com.example.book.error.DuplicateRecordException;
import com.example.book.repository.AutherRepo;
import com.example.book.repository.AutherSpec;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Service class for managing authors.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AutherService extends BaseService<Auther, Long> {

	//@Autowired or make it final and make @RequiredArgsConstructor
	private final AutherRepo autherRepo;

	//Logger log = LoggerFactory.getLogger(AutherService.class);

	/**
	 * Finds an author by ID.
	 * 
	 * @param id The ID of the author.
	 * @return The found author.
	 * It is annotated with @Cacheable to cache the result of the method 
	 * to improve performance by avoiding repeated database queries for the same data.
	 * The result is cached under a specific cache name ("findByAuthorId").
	 * The cache key is dynamically generated using the id's name.
	 */
	@Cacheable(value = "findByAuthorId", key = "#id")
	public Auther findById(Long id) {
		return super.findById(id);
	}

	/**
	 * Gets an author by ID.
	 * 
	 * @param id The ID of the author.
	 * @return The author.
	 */
	public Auther getById(Long id) {
		return super.getById(id);
	}

	/**
	 * This method retrieves all authors from the database.
	 * It is annotated with @Cacheable to cache the result of the method 
	 * to improve performance by avoiding repeated database queries for the same data.
	 * The result is cached under a specific cache name ("findAllAuthors").
	 * The cache key is dynamically generated using the method's name.
	 * 
	 * @return a list of all authors
	 */
	@Cacheable(value = "findAllAuthors", key = "#root.methodName")
	public List<Auther> findAll() {
		return super.findAll();
	}

	/**
	 * This method inserts a new author into the database.
	 * It performs the following checks before inserting:
	 * 1. Ensures the author's ID is null (since new entities should not have an ID).
	 * 2. Checks if the author's email is provided and not empty.
	 * 3. Verifies that the email is unique by checking for an existing author with the same email.
	 * If any validation fails, an exception is thrown.
	 * 
	 * The method is annotated with @CacheEvict to evict (clear) the specified cache entries when a new author is inserted.
	 * It evicts the caches "findAllAuthors" and "findByAuthorId" based on the method name as the cache key.
	 * allEntries = true it removes all related data from cache
	 * 
	 * @param entity the author entity to insert
	 * @return the inserted author
	 * @throws RuntimeException if the author's ID is not null or if the email is already in use
	 * @throws DuplicateRecordException if the email is already found in the database
	 */
	@CacheEvict(value = {"findAllAuthors" , "findByAuthorId" }, key = "#root.methodName", allEntries = true)
	public Auther insert(Auther entity) {
		if (entity.getId() != null) {
			throw new RuntimeException("ID must be null for new entities");
		}
		if (!entity.getEmail().isEmpty() && entity.getEmail() != null) {
			log.info("Author Name is {} Author Email is {}", entity.getName(), entity.getEmail());
			Optional<Auther> auther = findByEmail(entity.getEmail());
			if (auther.isPresent()) {
				log.error("This email :- " + entity.getEmail() + " is already found");
				throw new DuplicateRecordException("This email :- " + entity.getEmail() + " is already found");
			}
		}
		return super.insert(entity);
	}

	/**
	 * Inserts a list of authors.
	 * 
	 * @param entity The list of authors to insert.
	 * @return The inserted authors.
	 */
	@CacheEvict(value = {"findAllAuthors" , "findByAuthorId" }, key = "#root.methodName", allEntries = true)
	public List<Auther> insertAll(List<Auther> entity) {
		return super.insertAll(entity);
	}

	/**
	 * Updates an existing author.
	 * 
	 * @param entity The author to update.
	 * @return The updated author.
	 */
	@CacheEvict(value = {"findAllAuthors" , "findByAuthorId" }, key = "#root.methodName", allEntries = true)
	public Auther update(Auther entity) {
		Auther auther = findById(entity.getId());
		auther.setName(entity.getName());
		auther.setEmail(entity.getEmail());
		return autherRepo.save(auther);
	}

	/**
	 * Finds authors based on search criteria.
	 * 
	 * @param search The search criteria.
	 * @return A list of matching authors.
	 */
	
	public List<Auther> findByAutherSpec(AutherSearch search) {
		AutherSpec spec = new AutherSpec(search);
		return autherRepo.findAll(spec);
	}

	/**
	 * Deletes an author by ID.
	 * 
	 * @param id The ID of the author to delete.
	 */
	public void deleteById(Long id) {
		super.deleteById(id);
	}

	/**
	 * Finds an author by email.
	 * 
	 * @param email The email of the author.
	 * @return An optional containing the author if found.
	 */
	@Cacheable(value = "findByEmail", key = "#email")
	public Optional<Auther> findByEmail(String email) {
		return autherRepo.findByEmail(email);
	}
}