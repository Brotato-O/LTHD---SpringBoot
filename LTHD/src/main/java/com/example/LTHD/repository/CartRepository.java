package com.example.LTHD.repository;

import com.example.LTHD.entity.Cart;
import com.example.LTHD.entity.CartItems;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Cart> findByUserId(Long userId);
}