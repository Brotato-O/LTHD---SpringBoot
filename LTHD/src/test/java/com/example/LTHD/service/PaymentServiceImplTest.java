package com.example.LTHD.service;

import com.example.LTHD.entity.Order;
import com.example.LTHD.entity.Payment;
import com.example.LTHD.dto.response.PaymentResponse;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.PaymentMapper;
import com.example.LTHD.repository.OrderRepository;
import com.example.LTHD.repository.PaymentRepository;
import com.example.LTHD.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void confirmCODPayment_shouldMarkCompletedOrderAsPaid() {
        Order order = Order.builder().id(1L).status("COMPLETED").build();
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod("COD");
        payment.setStatus("UNPAID");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        paymentService.confirmCODPayment(1L);

        assertThat(payment.getStatus()).isEqualTo("PAID");
        assertThat(payment.getPaidAt()).isBeforeOrEqualTo(LocalDateTime.now());
        verify(paymentRepository).save(payment);
    }

    @Test
    void confirmCODPayment_shouldRejectNonCodPayment() {
        Order order = Order.builder().id(1L).status("COMPLETED").build();
        Payment payment = new Payment();
        payment.setPaymentMethod("VNPAY");
        payment.setStatus("UNPAID");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.confirmCODPayment(1L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PAYMENT_METHOD);

        assertThat(payment.getStatus()).isEqualTo("UNPAID");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void confirmCODPayment_shouldRejectCancelledOrder() {
        Order order = Order.builder().id(1L).status("CANCELLED").build();
        Payment payment = new Payment();
        payment.setPaymentMethod("COD");
        payment.setStatus("UNPAID");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.confirmCODPayment(1L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAYMENT_CANNOT_CONFIRM);

        assertThat(payment.getStatus()).isEqualTo("UNPAID");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void confirmCODPayment_shouldRejectMissingOrder() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.confirmCODPayment(99L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_NOT_FOUND);

        verifyNoInteractions(paymentRepository);
    }

    @Test
    void confirmCODPayment_shouldRejectMissingPayment() {
        Order order = Order.builder().id(1L).status("COMPLETED").build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.confirmCODPayment(1L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAYMENT_NOT_FOUND);

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void confirmCODPayment_shouldRejectIncompleteOrder() {
        Order order = Order.builder().id(1L).status("SHIPPING").build();
        Payment payment = new Payment();
        payment.setPaymentMethod("COD");
        payment.setStatus("UNPAID");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.confirmCODPayment(1L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAYMENT_CANNOT_CONFIRM);

        verify(paymentRepository, never()).save(payment);
    }

    @Test
    void confirmCODPayment_shouldRejectAlreadyPaidPayment() {
        Order order = Order.builder().id(1L).status("COMPLETED").build();
        Payment payment = new Payment();
        payment.setPaymentMethod("COD");
        payment.setStatus("PAID");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.confirmCODPayment(1L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAYMENT_ALREADY_PAID);
    }

    @Test
    void getPaymentById_shouldRejectMissingPayment() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentById(99L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAYMENT_NOT_FOUND);
    }

    @Test
    void getPaymentById_shouldReturnMappedPayment() {
        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toPaymentResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.getPaymentById(1L);

        assertThat(result).isSameAs(response);
    }

    @Test
    void getPaymentByOrderId_shouldReturnMappedPayment() {
        Order order = Order.builder().id(7L).build();
        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse();
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(7L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toPaymentResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.getPaymentByOrderId(7L);

        assertThat(result).isSameAs(response);
    }
}
