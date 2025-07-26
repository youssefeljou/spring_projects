/* src/main/java/com/example/ecommerce/service/order/OrderHistoryService.java */
package com.example.ecommerce.service.order;

import com.example.ecommerce.dto.OrderViewDto;
import com.example.ecommerce.mapper.OrderHistoryMapper;
import com.example.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

    private final OrderRepository orderRepo;
    private final OrderHistoryMapper mapper;   // <<< inject mapper

    public List<OrderViewDto> getHistory(Long customerId) {
        return orderRepo.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(mapper::toView)          // <<< MapStruct call
                .toList();
    }
}
