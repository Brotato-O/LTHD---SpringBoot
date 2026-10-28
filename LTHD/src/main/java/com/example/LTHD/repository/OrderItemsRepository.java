package com.example.LTHD.repository;

import com.example.LTHD.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemsRepository
        extends JpaRepository<OrderItems, Long> {

    // Lấy danh sách sản phẩm thuộc đơn hàng
    List<OrderItems> findByOrderId(Long orderId);
}