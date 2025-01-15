package com.example.book.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * A base repository interface that extends JpaRepository to provide CRUD operations
 * for any entity that extends BaseEntity. This repository is generic and can be used 
 * across different entities with different ID types.
 * 
 * @param <T> the type of the entity that extends BaseEntity
 * @param <ID> the type of the entity ID, which must be a subclass of Number
 */
@NoRepositoryBean // Prevents Spring Data JPA from creating a repository implementation for this interface
public interface BaseRepository<T extends BaseEntity<ID>, ID extends Number> extends JpaRepository<T, ID> {

    /*
        Example of how to define a custom update method:
        
        @Modifying
        @Transactional
        void updateStatus(@Param("id") ID id, @Param("statusCode") String statuscode);
    */
}
