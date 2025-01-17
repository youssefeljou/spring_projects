package com.example.book.config;

import java.util.Arrays;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.book.entity.Auther;
import com.example.book.entity.Book;
import com.example.book.service.AutherService;
import com.example.book.service.BookService;

import lombok.RequiredArgsConstructor;

/**
 * Component to initialize the application with default data on startup.
 */
@Component
@RequiredArgsConstructor
public class StartupApp implements CommandLineRunner {

	//@Autowired or make it final and make @RequiredArgsConstructor
    private final AutherService autherService;

   //@Autowired or make it final and make @RequiredArgsConstructor
    private final BookService bookService;

    /**
     * Method to run on application startup, initializing data if necessary.
     *
     * @param args command line arguments
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        if (autherService.findAll().isEmpty()) {
            Auther auther1 = new Auther();
            auther1.setName("Youssef");

            Auther auther2 = new Auther();
            auther2.setName("Adel");

            Auther auther3 = new Auther();
            auther3.setName("Ahmed");

            autherService.insertAll(Arrays.asList(auther1, auther2, auther3));
        }

        if (bookService.findAll().isEmpty()) {
            Book book1 = new Book();
            book1.setName("jpa");
            book1.setPrice(100);
            book1.setAuther(autherService.getById(1L));

            Book book2 = new Book();
            book2.setName("jdbc");
            book2.setPrice(200);
            book2.setAuther(autherService.getById(3L));

            Book book3 = new Book();
            book3.setName("sql");
            book3.setPrice(300);
            book3.setAuther(autherService.getById(3L));

            bookService.insertAll(Arrays.asList(book1, book2, book3));
        }
    }
}
