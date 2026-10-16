package com.example.LTHD.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.LTHD.dto.response.OrderItemResponse;
import com.example.LTHD.dto.response.OrderResponse;
import com.example.LTHD.entity.Order;
import com.example.LTHD.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemResponse toOrderItemResponse(OrderItem item);

    List<OrderItemResponse> toOrderItemResponseList(
            List<OrderItem> items);
}
