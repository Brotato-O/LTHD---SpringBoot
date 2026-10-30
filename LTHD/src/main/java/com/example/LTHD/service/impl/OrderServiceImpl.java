package com.example.LTHD.service.impl;

import com.example.LTHD.dto.request.CheckoutRequest;
import com.example.LTHD.dto.response.OrderResponse;

import com.example.LTHD.entity.*;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.OrderMapper;

import com.example.LTHD.repository.CartRepository;
import com.example.LTHD.repository.OrderRepository;
import com.example.LTHD.repository.ProductRepository;
import com.example.LTHD.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    // Đặt hàng từ giỏ hàng
    @Override
    @Transactional
    public OrderResponse checkout(
            Long userId,
            CheckoutRequest request) {

        // Giai đoạn đầu chỉ hỗ trợ COD
        if (!"COD".equals(request.getPaymentMethod())) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Order order = new Order();

        order.setUser(cart.getUser());
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod("COD");

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItems cartItem : cart.getItems()) {

            Product product = productRepository
                    .findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            int quantity = cartItem.getQuantity();

            if (quantity <= 0) {
                throw new AppException(ErrorCode.INVALID_QUANTITY);
            }

            // Lấy giá hiện tại từ database
            BigDecimal price = product.getPrice();

            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(quantity));

            OrderItems orderItem = new OrderItems();

            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(price);
            orderItem.setSubtotal(subtotal);

            order.addItem(orderItem);

            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);

        // Tạo thông tin thanh toán COD
        Payment payment = new Payment();

        payment.setPaymentMethod("COD");
        payment.setAmount(totalAmount);
        payment.setStatus("UNPAID");

        order.setPayment(payment);

        // Cascade lưu OrderItems và Payment
        Order savedOrder = orderRepository.saveAndFlush(order);

        // Xóa sản phẩm khỏi giỏ sau khi tạo đơn
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderMapper.toOrderResponse(savedOrder);
    }

    // Lấy lịch sử đơn hàng
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(Long userId) {

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    // Xem chi tiết đơn hàng
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(
            Long userId,
            Long orderId) {

        Order order = orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        return orderMapper.toOrderResponse(order);
    }

    // Người dùng hủy đơn hàng
    @Override
    @Transactional
    public OrderResponse cancelOrder(
            Long userId,
            Long orderId) {

        Order order = orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        order.setStatus(OrderStatus.CANCELLED);

        return orderMapper.toOrderResponse(
                orderRepository.save(order));
    }

    // Admin cập nhật trạng thái đơn hàng
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.CANCELLED
                || order.getStatus() == OrderStatus.COMPLETED) {
            throw new AppException(ErrorCode.ORDER_CANNOT_UPDATE);
        }

        order.setStatus(status);

        return orderMapper.toOrderResponse(
                orderRepository.save(order));
    }
}