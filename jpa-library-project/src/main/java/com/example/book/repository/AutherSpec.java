package com.example.book.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.book.entity.Auther;
import com.example.book.entity.AutherSearch;
import com.example.book.entity.Book;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

/**
 * Specification class for filtering {@link Auther} entities based on search
 * criteria.
 */
@RequiredArgsConstructor
public class AutherSpec implements Specification<Auther> {

	
	private final AutherSearch autherSearch;

	/**
	 * Constructor to initialize the search criteria.
	 *
	 * @param autherSearch the search criteria
	 */
	
	/*
	public AutherSpec(AutherSearch autherSearch) {
		this.autherSearch = autherSearch;
	}
*/
	
	/**
	 * Converts the search criteria into a {@link Predicate} for querying the
	 * database.
	 *
	 * @param root  the root type in the from clause
	 * @param query the query being created
	 * @param cb    the criteria builder
	 * @return a predicate representing the search criteria
	 */
	@Override
	public Predicate toPredicate(Root<Auther> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		// Join the Book entity
		Join<Auther, Book> bookJoin = root.join("books", JoinType.LEFT);

		// Author Name
		if (autherSearch.getAutherName() != null && !autherSearch.getAutherName().isEmpty()) {
			predicates.add(cb.like(root.get("name"), "%" + autherSearch.getAutherName() + "%"));
		}

		// Author Email
		if (autherSearch.getEmail() != null && !autherSearch.getEmail().isEmpty()) {
			predicates.add(cb.like(root.get("email"), "%" + autherSearch.getEmail() + "%"));
		}

		// Author IP Address
		if (autherSearch.getIpAddress() != null && !autherSearch.getIpAddress().isEmpty()) {
			predicates.add(cb.like(root.get("ipAddress"), "%" + autherSearch.getIpAddress() + "%"));
		}

		// Book Name
		if (autherSearch.getBookName() != null && !autherSearch.getBookName().isEmpty()) {
			predicates.add(cb.like(bookJoin.get("name"), "%" + autherSearch.getBookName() + "%"));
		}

		// Book Price
		if (autherSearch.getPrice() != null) {
			predicates.add(cb.greaterThanOrEqualTo(bookJoin.get("price"), autherSearch.getPrice()));
		}

		return cb.and(predicates.toArray(new Predicate[0]));
	}
}
