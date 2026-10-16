package com.example.LTHD.mapper;

import com.example.LTHD.dto.response.CartResponse;
import com.example.LTHD.dto.response.CartItemResponse;
import com.example.LTHD.entity.Cart;
import com.example.LTHD.entity.CartItems;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    // CartItems -> CartItemResponse
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(item))")
    CartItemResponse toCartItemResponse(CartItems item);

    // List<CartItems> -> List<CartItemResponse>
    List<CartItemResponse> toCartItemResponseList(
            List<CartItems> items);

    // Cart -> CartResponse
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalQuantity", expression = "java(calculateTotalQuantity(cart))")
    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(cart))")
    CartResponse toCartResponse(Cart cart);

    // Tính thành tiền từng sản phẩm
    default BigDecimal calculateSubtotal(CartItems item) {
        if (item == null ||
                item.getPrice() == null ||
                item.getQuantity() == null) {
            return BigDecimal.ZERO;
        }

        return item.getPrice().multiply(
                BigDecimal.valueOf(item.getQuantity()));
    }

    // Tính tổng số lượng sản phẩm
    default Integer calculateTotalQuantity(Cart cart) {
        if (cart == null || cart.getItems() == null) {
            return 0;
        }

        return cart.getItems()
                .stream()
                .mapToInt(item -> item.getQuantity() != null
                        ? item.getQuantity()
                        : 0)
                .sum();
    }

    // Tính tổng tiền giỏ hàng
    default BigDecimal calculateTotalAmount(Cart cart) {
        if (cart == null || cart.getItems() == null) {
            return BigDecimal.ZERO;
        }

        return cart.getItems()
                .stream()
                .map(this::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}