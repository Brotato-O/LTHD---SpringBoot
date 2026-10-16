package com.example.LTHD.controller;

import com.example.LTHD.dto.response.OrderResponse;
import com.example.LTHD.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}/checkout")
    public OrderResponse checkout(@PathVariable Long userId) {
        return orderService.checkout(userId);
    }
}
