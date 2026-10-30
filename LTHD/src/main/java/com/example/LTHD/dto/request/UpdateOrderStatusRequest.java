package com.example.LTHD.dto.request;

import com.example.LTHD.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UpdateOrderStatusRequest {

    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    private OrderStatus status;

    // Getter và Setter
}