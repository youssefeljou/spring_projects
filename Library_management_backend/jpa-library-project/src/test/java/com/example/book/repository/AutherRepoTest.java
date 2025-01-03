package com.example.book.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.book.entity.Auther;

@SpringBootTest
public class AutherRepoTest {

	@Autowired
	private AutherRepo autherRepo; 
	@Test
	void findByEmailNotFoundRepoTest()
	{
		Optional<Auther> auther=autherRepo.findByEmail("youssefeljou@gmail.com");
		assertEquals(false, auther.isPresent());
		
	}
}
