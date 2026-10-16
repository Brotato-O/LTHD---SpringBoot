package com.example.LTHD.service.impl;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import com.example.LTHD.dto.response.OrderResponse;
import com.example.LTHD.entity.Cart;
import com.example.LTHD.entity.CartItems;
import com.example.LTHD.entity.Order;
import com.example.LTHD.entity.OrderItem;
import com.example.LTHD.entity.Product;
import com.example.LTHD.entity.User;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.OrderMapper;
import com.example.LTHD.repository.CartRepository;
import com.example.LTHD.repository.OrderRepository;
import com.example.LTHD.repository.ProductRepository;
import com.example.LTHD.repository.UserRepository;
import com.example.LTHD.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse checkout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(com.example.LTHD.entity.OrderStatus.CONFIRMED);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<CartItems> cartItems = cart.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getId()))
                .toList();

        for (CartItems cartItem : cartItems) {
            Product product = productRepository
                    .findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() ->
                            new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            Integer quantity = cartItem.getQuantity();
            if (product.getStock() < quantity) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(quantity));

            product.setStock(product.getStock() - quantity);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(unitPrice);
            orderItem.setSubtotal(subtotal);
            order.addItem(orderItem);

            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        cart.getItems().clear();

        return orderMapper.toOrderResponse(savedOrder);
    }
}
