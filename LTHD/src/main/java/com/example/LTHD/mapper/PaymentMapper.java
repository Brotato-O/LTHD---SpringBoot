package com.example.LTHD.mapper;

import com.example.LTHD.dto.response.PaymentResponse;
import com.example.LTHD.entity.Payment;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    // Payment -> PaymentResponse
    PaymentResponse toPaymentResponse(Payment payment);

    // List<Payment> -> List<PaymentResponse>
    List<PaymentResponse> toPaymentResponseList(
            List<Payment> payments);
}