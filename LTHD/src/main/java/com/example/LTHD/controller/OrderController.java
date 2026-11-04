package com.example.LTHD.controller;

import com.example.LTHD.dto.request.CheckoutRequest;
import com.example.LTHD.dto.request.UpdateOrderStatusRequest;
import com.example.LTHD.dto.response.OrderResponse;
import com.example.LTHD.service.CurrentUserService;
import com.example.LTHD.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    // Đặt hàng từ giỏ hàng
    @PostMapping("/{userId}/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkout(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest request) {

        return orderService.checkout(userId, request);
    }

    // Lấy lịch sử đơn hàng của người dùng
    @GetMapping("/user/{userId}")
    public List<OrderResponse> getOrders(
            @PathVariable Long userId) {

        return orderService.getOrders(userId);
    }

    // Lấy chi tiết đơn hàng
    @GetMapping("/{userId}/{orderId}")
    public OrderResponse getOrderById(
            @PathVariable Long userId,
            @PathVariable Long orderId) {

        return orderService.getOrderById(userId, orderId);
    }

    // Người dùng hủy đơn hàng
    @PutMapping("/{userId}/{orderId}/cancel")
    public OrderResponse cancelOrder(
            @PathVariable Long userId,
            @PathVariable Long orderId) {

        return orderService.cancelOrder(userId, orderId);
    }

    // Admin cập nhật trạng thái đơn hàng
    @PutMapping("/admin/{orderId}")
    public OrderResponse updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return orderService.updateOrderStatus(orderId, request.getStatus());
    }
}