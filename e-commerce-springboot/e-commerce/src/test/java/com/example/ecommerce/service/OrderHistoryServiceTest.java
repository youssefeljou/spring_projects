package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderItemViewDto;
import com.example.ecommerce.dto.OrderViewDto;
import com.example.ecommerce.mapper.OrderHistoryMapper;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.order.OrderHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderHistoryServiceTest {

    private OrderRepository orderRepo;
    private OrderHistoryMapper mapper;
    private OrderHistoryService service;

    @BeforeEach
    void setUp() {
        orderRepo = mock(OrderRepository.class);
        mapper = mock(OrderHistoryMapper.class);
        service = new OrderHistoryService(orderRepo, mapper);
    }

    @Test
    void shouldReturnEmptyListWhenCustomerHasNoOrders() {
        Long customerId = 1L;

        when(orderRepo.findByCustomerIdOrderByCreatedAtDesc(customerId)).thenReturn(Collections.emptyList());

        List<OrderViewDto> result = service.getHistory(customerId);

        assertTrue(result.isEmpty());
        verify(orderRepo).findByCustomerIdOrderByCreatedAtDesc(customerId);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldMapSingleOrderCorrectly() {
        Long customerId = 2L;
        Order dummyOrder = new Order();
        dummyOrder.setId(100L);

        OrderViewDto expectedDto = new OrderViewDto(
                100L,
                null,                   // PaymentType, null for simplicity
                new BigDecimal("150.00"),
                "EGP",
                LocalDate.now(),
                List.of(new OrderItemViewDto(1L, "Mouse", new BigDecimal("150.00"), 1))
        );

        when(orderRepo.findByCustomerIdOrderByCreatedAtDesc(customerId)).thenReturn(List.of(dummyOrder));
        when(mapper.toView(dummyOrder)).thenReturn(expectedDto);

        List<OrderViewDto> result = service.getHistory(customerId);

        assertEquals(1, result.size());
        assertEquals(expectedDto.id(), result.get(0).id());

        verify(orderRepo).findByCustomerIdOrderByCreatedAtDesc(customerId);
        verify(mapper).toView(dummyOrder);
    }

    @Test
    void shouldMapMultipleOrders() {
        Long customerId = 3L;
        Order order1 = new Order(); order1.setId(1L);
        Order order2 = new Order(); order2.setId(2L);

        OrderViewDto dto1 = new OrderViewDto(1L, null, BigDecimal.TEN, "EGP", LocalDate.now(), List.of());
        OrderViewDto dto2 = new OrderViewDto(2L, null, BigDecimal.TEN, "EGP", LocalDate.now(), List.of());

        when(orderRepo.findByCustomerIdOrderByCreatedAtDesc(customerId)).thenReturn(List.of(order1, order2));
        when(mapper.toView(order1)).thenReturn(dto1);
        when(mapper.toView(order2)).thenReturn(dto2);

        List<OrderViewDto> result = service.getHistory(customerId);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }

    @Test
    void shouldHandleOrderWithNoItems() {
        Long customerId = 4L;
        Order emptyOrder = new Order(); emptyOrder.setId(42L);

        OrderViewDto dto = new OrderViewDto(42L, null, BigDecimal.ZERO, "EGP", LocalDate.now(), List.of());

        when(orderRepo.findByCustomerIdOrderByCreatedAtDesc(customerId)).thenReturn(List.of(emptyOrder));
        when(mapper.toView(emptyOrder)).thenReturn(dto);

        List<OrderViewDto> result = service.getHistory(customerId);

        assertEquals(1, result.size());
        assertEquals(42L, result.get(0).id());
        assertTrue(result.get(0).items().isEmpty());
    }
}
