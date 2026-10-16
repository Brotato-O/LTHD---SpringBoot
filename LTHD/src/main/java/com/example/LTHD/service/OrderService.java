package com.example.LTHD.service;

import com.example.LTHD.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse checkout(Long userId);
}
