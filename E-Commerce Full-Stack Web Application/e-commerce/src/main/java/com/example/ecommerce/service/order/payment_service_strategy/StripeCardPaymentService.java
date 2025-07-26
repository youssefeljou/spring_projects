package com.example.ecommerce.service.order.payment_service_strategy;

import com.example.ecommerce.dto.CreditCardCreateDTO;
import com.example.ecommerce.dto.PaymentRequestDto;
import com.example.ecommerce.dto.PaymentResultDto;
import com.example.ecommerce.enums.PaymentType;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.mapper.CreditCardMapper;
import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.ShoppingCart;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.util.CardUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeCardPaymentService implements PaymentStrategy {

    private final ShoppingCartRepository cartRepo;
    private final CustomerRepository     custRepo;
    private final CreditCardRepository   cardRepo;
    private final OrderRepository        orderRepo;
    private final CreditCardMapper       mapper;

    @Override
    public PaymentResultDto pay(Long cartId, PaymentRequestDto req) {

        ShoppingCart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (orderRepo.existsByShoppingCart(cart))
            throw new InvalidInputException("Payment already processed for this cart.");

        var customer = custRepo.findById(cart.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        String token;
        CreditCard newCardEntity = null;

        if (req.simulateFailure()) {
            token = CardUtil.failureToken();
        }
        else if (req.cardId() != null) {
            CreditCard cc = cardRepo.findById(req.cardId())
                    .orElseThrow(() -> new InvalidInputException("Card not found"));
            token = CardUtil.successToken(cc.getBrand());
        }
        else {
            String mm   = String.format("%02d", req.expMonth());
            String yy   = String.format("%02d", req.expYear() % 100);
            String exp  = mm + "/" + yy;

            CreditCardCreateDTO dto = new CreditCardCreateDTO(
                    req.number(), req.cardHolderName(), exp);

            try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
                Validator v = vf.getValidator();
                var violations = v.validate(dto);
                if (!violations.isEmpty())
                    throw new InvalidInputException(violations.iterator().next().getMessage());
            }

            if (!CardUtil.passesLuhn(req.number()) ||
                    "UNKNOWN".equals(CardUtil.brand(req.number())))
                throw new InvalidInputException("Please enter a valid card number");

            YearMonth expYm = YearMonth.of(req.expYear(), req.expMonth());
            if (YearMonth.now().isAfter(expYm))
                throw new InvalidInputException("Card is expired");

            if (req.cvc() == null || !req.cvc().matches("\\d{3}"))
                throw new InvalidInputException("CVV must be 3 digits");

            String brand = CardUtil.brand(req.number());
            token = CardUtil.successToken(brand);

            newCardEntity = mapper.toEntity(dto);
            newCardEntity.setCustomer(customer);
        }

        try {
            long cents = BigDecimal.valueOf(cart.getTotal()).movePointRight(2).longValue();

            Charge charge = Charge.create(ChargeCreateParams.builder()
                    .setAmount(cents)
                    .setCurrency("usd")
                    .setSource(token)
                    .setDescription("Stripe sandbox charge")
                    .build());

            if (!"succeeded".equals(charge.getStatus()))
                throw new InvalidInputException("Stripe charge failed");

            if (newCardEntity != null) {
                cardRepo.save(newCardEntity);
            }

            Order order = new Order();
            order.setShoppingCart(cart);
            order.setCustomer(customer);
            order.setPaymentType(PaymentType.CARD);
            order.setAmount(BigDecimal.valueOf(cart.getTotal()));
            order.setCurrency("usd");
            order.setPaymentIntentId(charge.getId());
            order.setOrderDate(LocalDate.now());

            List<OrderItem> orderItems = cart.getItems().stream()
                    .map(ci -> {
                        OrderItem oi = new OrderItem();
                        oi.setOrder(order);
                        oi.setProductId(ci.getProduct().getId());
                        oi.setProductName(ci.getProduct().getName());
                        oi.setUnitPrice(BigDecimal.valueOf(ci.getProduct().getPrice()));
                        oi.setQuantity(ci.getQuantity());
                        return oi;
                    })
                    .toList();

            order.setItems(orderItems);

            orderRepo.save(order);
            order.setShoppingCart(null);
            orderRepo.save(order);

            cartRepo.deleteById(cartId);
            customer.setShoppingCart(null);
            custRepo.save(customer);


            return new PaymentResultDto("DONE", "Payment succeeded");

        } catch (StripeException e) {
//            log.error("Stripe exception occurred during payment", e);
            throw new InvalidInputException("Payment processing failed. Please try again or use a different card.");
        }
    }
}
