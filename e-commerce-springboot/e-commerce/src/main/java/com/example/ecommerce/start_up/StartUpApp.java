package com.example.ecommerce.start_up;

import com.example.ecommerce.enums.Category;
import com.example.ecommerce.enums.ProductStatus;
import com.example.ecommerce.enums.Role;
import com.example.ecommerce.enums.Status;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.AdminRepository;
import com.example.ecommerce.repository.CreditCardRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartUpApp implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartUpApp.class);

    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final CreditCardRepository creditCardRepository;


    @Override
    public void run(String... args) {

        if (customerRepository.count() == 0 && adminRepository.count() == 0) {
            // Create Fady
            Customer customer = new Customer();
            customer.setUsername("fady");
            customer.setEmail("fady@example.com");
            customer.setPassword(passwordEncoder.encode("Fady@5678"));
            customer.setName("Fady");
            customer.setPhone(Collections.singletonList("01012345678"));
            customer.setRole(Role.CUSTOMER);
            customer.setAccountStatus(Status.ACTIVATED);
            customer.setShoppingCart(new ShoppingCart()); // empty for now
            customerRepository.save(customer);

            // Create Reem
            Customer deactivatedCustomer = new Customer();
            deactivatedCustomer.setUsername("reem");
            deactivatedCustomer.setEmail("reem@example.com");
            deactivatedCustomer.setPassword(passwordEncoder.encode("Reem@5678"));
            deactivatedCustomer.setName("Reem");
            deactivatedCustomer.setPhone(Collections.singletonList("01099998888"));
            deactivatedCustomer.setRole(Role.CUSTOMER);
            deactivatedCustomer.setAccountStatus(Status.DEACTIVATED);
            deactivatedCustomer.setShoppingCart(new ShoppingCart());
            customerRepository.save(deactivatedCustomer);

            // Admins
            Admin admin = new Admin();
            admin.setUsername("youssef");
            admin.setEmail("youssef@example.com");
            admin.setPassword(passwordEncoder.encode("Youssef@5678"));
            admin.setName("Youssef");
            admin.setPhone(Collections.singletonList("01087654321"));
            admin.setRole(Role.ADMIN);
            adminRepository.save(admin);

            Admin superAdmin = new Admin();
            superAdmin.setUsername("yasser");
            superAdmin.setEmail("yasser@example.com");
            superAdmin.setPassword(passwordEncoder.encode("Yasser@5678"));
            superAdmin.setName("Yasser");
            superAdmin.setPhone(Collections.singletonList("01000000000"));
            superAdmin.setRole(Role.SUPERADMIN);
            adminRepository.save(superAdmin);

            // Cards
            CreditCard validCard = new CreditCard();
            validCard.setCustomer(customer);
            validCard.setCardHolderName("Fady User");
            validCard.setPlainCardNumber("4111111111111111");
            validCard.setExpiration(YearMonth.of(2026, 12));
            creditCardRepository.save(validCard);

            CreditCard invalidCard = new CreditCard();
            invalidCard.setCustomer(customer);
            invalidCard.setCardHolderName("Fady Invalid");
            invalidCard.setPlainCardNumber("6011000990139424");
            invalidCard.setExpiration(YearMonth.of(2026, 12));
            creditCardRepository.save(invalidCard);

            log.info("✅ Test credit cards (valid & invalid) saved for customer Fady.");
            log.info("✅ Initial users (Fady, Reem, Youssef, Yasser) created successfully.");
        }
        if (productRepository.count() == 0) {
            if (productRepository.count() == 0) {
                Product p1 = new Product();
                p1.setName("Wireless Earbuds");
                p1.setDescription("Compact Bluetooth 5.0 earbuds with noise cancellation.");
                p1.setPrice(750.0f);
                p1.setCategory(Category.ELECTRONICS);
                p1.setStockQuantity(50);
                p1.setStatus(ProductStatus.ACTIVE);
                p1.setImage("https://m.media-amazon.com/images/I/61Mdr2D7APL._AC_SL1500_.jpg");

                Product p2 = new Product();
                p2.setName("Smart Light Bulb");
                p2.setDescription("WiFi-enabled RGB LED bulb, voice assistant compatible.");
                p2.setPrice(150.0f);
                p2.setCategory(Category.SMART_HOME);
                p2.setStockQuantity(40);
                p2.setStatus(ProductStatus.ACTIVE);
                p2.setImage("https://m.media-amazon.com/images/I/61mvjRZCJkL._AC_SL1500_.jpg");

                Product p3 = new Product();
                p3.setName("Smart Thermostat");
                p3.setDescription("Smart thermostat with remote control and scheduling.");
                p3.setPrice(1200.0f);
                p3.setCategory(Category.SMART_HOME);
                p3.setStockQuantity(25);
                p3.setStatus(ProductStatus.ACTIVE);
                p3.setImage("https://m.media-amazon.com/images/I/71GfTgWZnmL._AC_SL1500_.jpg");

                Product p4 = new Product();
                p4.setName("Men's Hoodie");
                p4.setDescription("Premium cotton hoodie for men, multiple colors.");
                p4.setPrice(300.0f);
                p4.setCategory(Category.FASHION);
                p4.setStockQuantity(100);
                p4.setStatus(ProductStatus.ACTIVE);
                p4.setImage("https://m.media-amazon.com/images/I/61z7tsKxdjL._AC_UL1500_.jpg");

                Product p5 = new Product();
                p5.setName("Women's Sneakers");
                p5.setDescription("Stylish running shoes for women with soft sole.");
                p5.setPrice(500.0f);
                p5.setCategory(Category.FASHION);
                p5.setStockQuantity(80);
                p5.setStatus(ProductStatus.ACTIVE);
                p5.setImage("https://m.media-amazon.com/images/I/71h8kUs2AeL._AC_UL1500_.jpg");

                Product p6 = new Product();
                p6.setName("Bluetooth Speaker");
                p6.setDescription("Portable waterproof speaker with powerful bass.");
                p6.setPrice(850.0f);
                p6.setCategory(Category.ELECTRONICS);
                p6.setStockQuantity(40);
                p6.setStatus(ProductStatus.ACTIVE);
                p6.setImage("https://m.media-amazon.com/images/I/61sdykLQJpL._AC_SL1500_.jpg");

                Product p7 = new Product();
                p7.setName("Gaming Mouse");
                p7.setDescription("Ergonomic gaming mouse with RGB lighting.");
                p7.setPrice(450.0f);
                p7.setCategory(Category.ELECTRONICS);
                p7.setStockQuantity(70);
                p7.setStatus(ProductStatus.ACTIVE);
                p7.setImage("https://m.media-amazon.com/images/I/61mpMH5TzkL._AC_SL1500_.jpg");

                Product p8 = new Product();
                p8.setName("Laptop Stand");
                p8.setDescription("Aluminum adjustable laptop stand for desks.");
                p8.setPrice(600.0f);
                p8.setCategory(Category.ELECTRONICS);
                p8.setStockQuantity(30);
                p8.setStatus(ProductStatus.ACTIVE);
                p8.setImage("https://m.media-amazon.com/images/I/61Ipi9n5mKL._AC_SL1500_.jpg");

                productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8));
                log.info("✅ Sample products inserted successfully.");
            }
        } else {
            log.info("ℹ️ Products already exist. Initialization skipped.");
        }

        // 👉 Add shopping cart data for Fady after products exist
        Customer fady = customerRepository.findByUsername("fady").orElse(null);
        if (fady != null && fady.getShoppingCart() != null) {
            List<Product> products = productRepository.findAll();
            if (products.size() >= 2) {
                ShoppingCart fadyCart = fady.getShoppingCart();
                List<CartItem> items = new ArrayList<>();

                CartItem item1 = new CartItem();
                item1.setProduct(products.get(0)); // Wireless Earbuds
                item1.setQuantity(2);
                item1.setCart(fadyCart);
                items.add(item1);

                CartItem item2 = new CartItem();
                item2.setProduct(products.get(1)); // Smart Light Bulb
                item2.setQuantity(1);
                item2.setCart(fadyCart);
                items.add(item2);

                float total = 0f;
                for (CartItem item : items) {
                    total += item.getProduct().getPrice() * item.getQuantity();
                }

                fadyCart.setItems(items);
                fadyCart.setCustomerId(fady.getId());
                fadyCart.setTotal(total);
                fadyCart.setExpiresAt(LocalDateTime.now().plusDays(7));

                fady.setShoppingCart(fadyCart);
                customerRepository.save(fady);
                log.info("✅ Fady's shopping cart populated with products and total calculated.");
            }
        }
    }
}