package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.OrderItemViewDto;
import com.example.ecommerce.dto.OrderViewDto;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderHistoryMapper {

    OrderViewDto toView(Order order);

    OrderItemViewDto toItemView(OrderItem item);

    List<OrderItemViewDto> toItemViewList(List<OrderItem> items);
}
