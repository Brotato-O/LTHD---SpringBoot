package com.example.LTHD.repository;

import com.example.LTHD.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemsRepository
                extends JpaRepository<CartItems, Integer> {

        Optional<CartItems> findByCartIdAndProductId(
                        Long cartId,
                        Integer productId);
}