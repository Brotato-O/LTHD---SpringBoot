package com.example.LTHD.mapper;

import com.example.LTHD.dto.response.OrderResponse;
import com.example.LTHD.dto.response.OrderItemResponse;
import com.example.LTHD.entity.Order;
import com.example.LTHD.entity.OrderItems;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { PaymentMapper.class })
public interface OrderMapper {

    // OrderItems -> OrderItemResponse
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemResponse toOrderItemResponse(OrderItems item);

    // List<OrderItems> -> List<OrderItemResponse>
    List<OrderItemResponse> toOrderItemResponseList(
            List<OrderItems> items);

    // Order -> OrderResponse
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "payment", source = "payment")
    OrderResponse toOrderResponse(Order order);

    // List<Order> -> List<OrderResponse>
    List<OrderResponse> toOrderResponseList(
            List<Order> orders);
}