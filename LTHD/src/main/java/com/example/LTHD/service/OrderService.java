package com.example.LTHD.service;

import com.example.LTHD.dto.request.CheckoutRequest;
import com.example.LTHD.dto.response.OrderResponse;
import com.example.LTHD.entity.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse checkout(Long userId, CheckoutRequest request);

    List<OrderResponse> getOrders(Long userId);

    OrderResponse getOrderById(Long userId, Long orderId);

    OrderResponse cancelOrder(Long userId, Long orderId);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
}