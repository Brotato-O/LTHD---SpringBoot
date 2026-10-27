package com.example.LTHD.repository;

import com.example.LTHD.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    // Tìm thanh toán theo đơn hàng
    Optional<Payment> findByOrderId(Long orderId);

    // Tìm thanh toán theo mã giao dịch
    Optional<Payment> findByTransactionId(String transactionId);
}