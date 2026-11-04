package com.example.LTHD.service.impl;

import com.example.LTHD.dto.response.PaymentResponse;

import com.example.LTHD.entity.Order;
import com.example.LTHD.entity.OrderStatus;
import com.example.LTHD.entity.Payment;

import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;

import com.example.LTHD.mapper.PaymentMapper;

import com.example.LTHD.repository.OrderRepository;
import com.example.LTHD.repository.PaymentRepository;

import com.example.LTHD.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

        private final PaymentRepository paymentRepository;
        private final OrderRepository orderRepository;
        private final PaymentMapper paymentMapper;

        // Lấy thông tin thanh toán theo ID
        @Override
        @Transactional(readOnly = true)
        public PaymentResponse getPaymentById(Long id) {

                Payment payment = paymentRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

                return paymentMapper.toPaymentResponse(payment);
        }

        // Lấy thông tin thanh toán theo đơn hàng
        @Override
        @Transactional(readOnly = true)
        public PaymentResponse getPaymentByOrderId(Long orderId) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

                Payment payment = paymentRepository
                                .findByOrderId(order.getId())
                                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

                return paymentMapper.toPaymentResponse(payment);
        }

        // Admin xác nhận thanh toán COD
        @Override
        @Transactional
        public PaymentResponse confirmCODPayment(Long orderId) {

                // Kiểm tra đơn hàng
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

                // Kiểm tra thanh toán
                Payment payment = paymentRepository
                                .findByOrderId(orderId)
                                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

                // Chỉ xử lý COD
                if (!"COD".equals(payment.getPaymentMethod())) {
                        throw new AppException(
                                        ErrorCode.INVALID_PAYMENT_METHOD);
                }

                // Kiểm tra đã thanh toán chưa
                if ("PAID".equals(payment.getStatus())) {
                        throw new AppException(
                                        ErrorCode.PAYMENT_ALREADY_PAID);
                }

                // Không cho thanh toán đơn đã hủy
                if (order.getStatus() == OrderStatus.CANCELLED) {
                        throw new AppException(
                                ErrorCode.PAYMENT_CANNOT_CONFIRM);
                }

                // Chỉ xác nhận khi đơn hàng hoàn thành
                if (order.getStatus() != OrderStatus.COMPLETED) {
                        throw new AppException(
                                ErrorCode.PAYMENT_CANNOT_CONFIRM);
                }

                // Cập nhật thanh toán
                payment.setStatus("PAID");
                payment.setPaidAt(LocalDateTime.now());

                Payment savedPayment = paymentRepository.save(payment);

                return paymentMapper.toPaymentResponse(savedPayment);
        }
}