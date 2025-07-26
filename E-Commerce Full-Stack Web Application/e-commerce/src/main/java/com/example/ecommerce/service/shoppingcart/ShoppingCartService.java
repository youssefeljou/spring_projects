package com.example.ecommerce.service.shoppingcart;

import com.example.ecommerce.dto.AddToCartRequestDto;
import com.example.ecommerce.dto.ProductPublicDTO;
import com.example.ecommerce.dto.UpdateCartRequestDto;
import com.example.ecommerce.enums.ProductStatus;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ShoppingCart;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Value("${ecommerce.tax.rate}")
    private float taxRate;

    @Transactional
    public void addProductToCart(Long customerId, AddToCartRequestDto request) {

        //check if the product exists in database//
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStatus() == ProductStatus.DELETED) {
            throw new InvalidInputException("This product is no longer available");
        }

        //check if the requested quantity is in stock//
        if (request.getQuantity() > product.getStockQuantity()) {
            throw new InvalidInputException("Not enough stock available");
        }

        //check if the customer exists//
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        //create a shopping cart for the customer//
        ShoppingCart shoppingCart = customer.getShoppingCart();
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setCustomerId(customer.getId());
            shoppingCart.setTotal(0f);
            shoppingCartRepository.save(shoppingCart);
            customer.setShoppingCart(shoppingCart);
            customerRepository.save(customer);
        }

        Optional<CartItem> existingItemOpt = shoppingCart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (product.getStatus() == ProductStatus.DISCONTINUED && existingItemOpt.isEmpty()) {
            throw new InvalidInputException("This product is discontinued and cannot be added");
        }

        //in case it already exists//
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            //make sure it's in stock again//
            if (newQuantity > product.getStockQuantity()) {
                throw new InvalidInputException("Not enough stock to add more of this item");
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else{
            CartItem newItem = new CartItem();
            newItem.setCart(shoppingCart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            shoppingCart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }
        //call function to calculate total price with taxes//
        recalculateCartTotalWithTax(shoppingCart);

        shoppingCart.setExpiresAt(LocalDateTime.now().plusDays(1));
        shoppingCartRepository.save(shoppingCart);
    }

    //a function to clear expired carts//
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanExpiredCarts() {
        List<ShoppingCart> expiredCarts = shoppingCartRepository.findAll()
                .stream()
                .filter(cart -> cart.getExpiresAt() != null && cart.getExpiresAt().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        for (ShoppingCart cart : expiredCarts) {
            cartItemRepository.deleteAll(cart.getItems());
            shoppingCartRepository.delete(cart);
        }
    }

    //function to delete the whole cart//
    @Transactional
    public void clearCart(Long shoppingCartId){
        //check for shopping cart existence//
        ShoppingCart cart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping cart not found"));
        //check if a customer has that cart//
        Customer customer = customerRepository.findByShoppingCart_Id(shoppingCartId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not associated with this cart"));
        shoppingCartRepository.deleteById(shoppingCartId);
        customer.setShoppingCart(null);
        customerRepository.save(customer);
    }

    //function to delete items from cart//
    @Transactional
    public void deleteProductFromCart(Long customerId, Long productId){

        //check for customer//
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        //get the cart of that customer//
        ShoppingCart cart= customer.getShoppingCart();
        if(cart == null || cart.getItems().isEmpty()){
            throw new InvalidInputException("Shopping cart is empty");
        }

        //find cart item of that product
        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        if (itemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Product not found in the cart");
        }

        //check if the total number more than one then decrease quantity by 1//
        CartItem cartItem = itemOpt.get();
        if (cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            cartItemRepository.save(cartItem);
        }else{
            //remove item from cart//
            cart.getItems().remove(cartItem);
            //remove item from database//
            cartItemRepository.delete(cartItem);
        }

        recalculateCartTotalWithTax(cart);
    }

    @Transactional
    public void updateCart(Long customerId,UpdateCartRequestDto request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        ShoppingCart cart = customer.getShoppingCart();
        if (cart == null) {
            throw new ResourceNotFoundException("Shopping cart not found for customer");
        }

        //check if it's available//
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStatus() == ProductStatus.DELETED) {
            throw new InvalidInputException("This product is no longer available");
        }

        //product must be already in the cart//
        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (itemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Product not found in the cart");}

        CartItem cartItem = itemOpt.get();
        int existingQuantity = cartItem.getQuantity();
        int newQuantity = request.getQuantity();
        //Negative values are not allowed//
        if (newQuantity < 0) {
            throw new InvalidInputException("Quantity must be zero or more");
        }

        if(newQuantity ==0){
            removeProductCompletelyFromCart(customerId, product.getId());
            return;
        }

        //the new updated quantity not available in stock//
        if (newQuantity > product.getStockQuantity()) {
            throw new InvalidInputException("Not enough stock available");
        }

        if (product.getStatus() == ProductStatus.DISCONTINUED && newQuantity > existingQuantity) {
            throw new InvalidInputException("Cannot increase quantity of a discontinued product");
        }

        if (newQuantity == existingQuantity) {
            throw new InvalidInputException("You can't enter the same Quantity");
        }
        if (newQuantity > existingQuantity) {
            int diff = newQuantity - existingQuantity;
            AddToCartRequestDto addRequest = new AddToCartRequestDto(product.getId(), diff);
            addProductToCart(customerId, addRequest);
        }else {
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
            recalculateCartTotalWithTax(cart);
        }
    }

    //helper function to delete specific product from cart completely//
    //this is called in update function when quantity of a product is set to 0//
    @Transactional
    public void removeProductCompletelyFromCart(Long customerId, Long productId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        ShoppingCart cart = customer.getShoppingCart();
        if (cart == null || cart.getItems().isEmpty()) {
            throw new InvalidInputException("Shopping cart is empty");
        }

        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Product not found in the cart");
        }

        CartItem cartItem = itemOpt.get();
        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        recalculateCartTotalWithTax(cart);
    }

    //function to calculate taxes to avoid code redundancy//
    private void recalculateCartTotalWithTax(ShoppingCart cart) {
        Float total = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice() * item.getQuantity())
                .reduce(0f, Float::sum);
        float totalWithTax = total + (total * taxRate);
        cart.setTotal(totalWithTax);
        shoppingCartRepository.save(cart);
    }
}
