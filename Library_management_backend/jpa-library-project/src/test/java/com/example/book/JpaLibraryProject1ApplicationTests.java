package com.example.book;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.book.entity.Auther;
import com.example.book.service.AutherServiceTest;


@SpringBootTest
class JpaLibraryProject1ApplicationTests {

	@Autowired
	private AutherServiceTest autherService;
	@Test
	void contextLoads() {
		
	}

	
}
