package com.example.LTHD.repository;

import com.example.LTHD.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Lấy danh sách đơn hàng của người dùng
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Lấy chi tiết đơn hàng theo ID và người dùng
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    // Lấy danh sách đơn hàng theo trạng thái
    List<Order> findByStatus(String status);
}