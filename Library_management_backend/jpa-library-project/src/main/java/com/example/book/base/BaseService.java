package com.example.book.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.example.book.config.MessageUtils;
import com.example.book.error.RecordNotFoundException;

import jakarta.persistence.MappedSuperclass;
import lombok.RequiredArgsConstructor;

/**
 * Base service class providing common business logic operations for entities.
 * Includes CRUD operations like find, insert, update, delete, and custom status
 * updates. This class should be extended by specific service classes to provide
 * entity-specific functionality.
 *
 * @param <T>  the type of the entity that extends BaseEntity
 * @param <ID> the type of the entity ID, which must be a subclass of Number
 */
@MappedSuperclass

public class BaseService<T extends BaseEntity<ID>, ID extends Number> {

	 @Autowired //or make it final and make @RequiredArgsConstructor
	private  BaseRepository<T, ID> baseRepository;

	 @Autowired //or make it final and make @RequiredArgsConstructor@Autowired
	private  MessageUtils messageUtils;

	/**
	 * Find an entity by its ID.
	 * 
	 * @param id the ID of the entity
	 * @return the entity if found
	 * @throws RecordNotFoundException if the entity is not found
	 */
	public T findById(ID id) {
		Optional<T> entity = baseRepository.findById(id);
		if (entity.isPresent()) {
			return entity.get();
		} else {
			String[] msgParam = { id.toString() };
			String msg = messageUtils.getMessage("validation.recoredNotFound.message", msgParam);
			throw new RecordNotFoundException(msg);
		}
	}

	/**
	 * Retrieve an entity by its ID, using getById (doesn't check existence).
	 *
	 * @param id the ID of the entity
	 * @return the entity
	 */
	public T getById(ID id) {
		return baseRepository.getById(id);
	}

	/**
	 * Find all entities.
	 *
	 * @return a list of all entities
	 */
	public List<T> findAll() {
		return baseRepository.findAll();
	}

	/**
	 * Insert a new entity. Throws an exception if the entity already has an ID.
	 *
	 * @param entity the entity to insert
	 * @return the saved entity
	 * @throws RuntimeException if the entity already has an ID
	 */
	public T insert(T entity) {
		if (entity.getId() != null) {
			throw new RuntimeException();
		}
		return baseRepository.save(entity);
	}

	/**
	 * Insert multiple entities at once.
	 *
	 * @param entity a list of entities to insert
	 * @return a list of saved entities
	 */
	public List<T> insertAll(List<T> entity) {
		return baseRepository.saveAll(entity);
	}

	/**
	 * Update an existing entity.
	 *
	 * @param entity the entity to update
	 * @return the updated entity
	 */
	public T update(T entity) {
		return baseRepository.save(entity);
	}

	/**
	 * Delete an entity by its ID.
	 *
	 * @param id the ID of the entity to delete
	 */
	public void deleteById(ID id) {
		baseRepository.deleteById(id);
	}

	/**
	 * Update the status of an entity by its ID. This is a custom method that
	 * modifies the status of an entity.
	 *
	 * @param id     the ID of the entity
	 * @param status the new status value
	 */
	@Transactional
	@Modifying
	public void updateStatusById(ID id, String status) {
		T entity = baseRepository.findById(id).orElseThrow(() -> new RuntimeException("Entity not found"));
		entity.setStatus(status);
		baseRepository.save(entity);
	}
}