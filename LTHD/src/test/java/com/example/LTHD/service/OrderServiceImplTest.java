package com.example.LTHD.service;

import com.example.LTHD.dto.request.CheckoutRequest;
import com.example.LTHD.dto.response.OrderResponse;
import com.example.LTHD.entity.Cart;
import com.example.LTHD.entity.CartItems;
import com.example.LTHD.entity.Order;
import com.example.LTHD.entity.Product;
import com.example.LTHD.entity.User;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.OrderMapper;
import com.example.LTHD.repository.CartRepository;
import com.example.LTHD.repository.OrderRepository;
import com.example.LTHD.repository.ProductRepository;
import com.example.LTHD.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void checkout_shouldCreateOrderCalculateTotalAndClearCart() {
        Product product = Product.builder()
                .id(10)
                .price(new BigDecimal("25.50"))
                .stock(10)
                .build();
        Cart cart = Cart.builder().id(1L).user(new User()).build();
        cart.addItem(CartItems.builder().product(product).quantity(2).build());
        CheckoutRequest request = checkoutRequest("COD");
        Order savedOrder = new Order();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findByIdForUpdate(10)).thenReturn(Optional.of(product));
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toOrderResponse(savedOrder)).thenReturn(new OrderResponse());

        orderService.checkout(1L, request);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).saveAndFlush(captor.capture());
        Order order = captor.getValue();
        assertThat(order.getTotalAmount()).isEqualByComparingTo("51.00");
        assertThat(order.getStatus()).isEqualTo("PENDING");
        assertThat(order.getShippingAddress()).isEqualTo("123 Main Street");
        assertThat(order.getPayment().getPaymentMethod()).isEqualTo("COD");
        assertThat(order.getPayment().getAmount()).isEqualByComparingTo("51.00");
        assertThat(order.getPayment().getStatus()).isEqualTo("UNPAID");
        assertThat(cart.getItems()).isEmpty();
        verify(cartRepository).save(cart);
    }

    @Test
    void checkout_shouldRejectUnsupportedPaymentMethod() {
        CheckoutRequest request = checkoutRequest("VNPAY");

        assertThatThrownBy(() -> orderService.checkout(1L, request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PAYMENT_METHOD);

        verifyNoInteractions(cartRepository, productRepository, orderRepository);
    }

    @Test
    void checkout_shouldRejectEmptyCart() {
        Cart cart = Cart.builder().id(1L).user(new User()).build();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.checkout(1L, checkoutRequest("COD")))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.CART_EMPTY);

        verifyNoInteractions(productRepository, orderRepository);
    }

    @Test
    void checkout_shouldRejectInvalidQuantity() {
        Product product = Product.builder()
                .id(10)
                .price(new BigDecimal("25.50"))
                .stock(10)
                .build();
        Cart cart = Cart.builder().id(1L).user(new User()).build();
        cart.addItem(CartItems.builder().product(product).quantity(0).build());
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findByIdForUpdate(10)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.checkout(1L, checkoutRequest("COD")))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_QUANTITY);

        verify(orderRepository, never()).saveAndFlush(any(Order.class));
    }

    @Test
    void getOrders_shouldReturnMappedOrdersInRepositoryOrder() {
        Order first = Order.builder().id(1L).build();
        Order second = Order.builder().id(2L).build();
        OrderResponse firstResponse = new OrderResponse();
        OrderResponse secondResponse = new OrderResponse();
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(first, second));
        when(orderMapper.toOrderResponse(first)).thenReturn(firstResponse);
        when(orderMapper.toOrderResponse(second)).thenReturn(secondResponse);

        List<OrderResponse> result = orderService.getOrders(1L);

        assertThat(result).containsExactly(firstResponse, secondResponse);
    }

    @Test
    void getOrderById_shouldRejectOrderNotOwnedByUser() {
        when(orderRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(1L, 5L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_NOT_FOUND);

        verifyNoInteractions(orderMapper);
    }

    @Test
    void cancelOrder_shouldCancelPendingOrder() {
        Order order = Order.builder().id(5L).status("PENDING").build();
        when(orderRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toOrderResponse(order)).thenReturn(new OrderResponse());

        orderService.cancelOrder(1L, 5L);

        assertThat(order.getStatus()).isEqualTo("CANCELLED");
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_shouldRejectCompletedOrder() {
        Order order = Order.builder().id(5L).status("COMPLETED").build();
        when(orderRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 5L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_CANNOT_CANCEL);

        verify(orderRepository, never()).save(order);
    }

    @Test
    void updateOrderStatus_shouldUpdatePendingOrder() {
        Order order = Order.builder().id(5L).status("PENDING").build();
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toOrderResponse(order)).thenReturn(new OrderResponse());

        orderService.updateOrderStatus(5L, "CONFIRMED");

        assertThat(order.getStatus()).isEqualTo("CONFIRMED");
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_shouldRejectInvalidStatus() {
        Order order = Order.builder().id(5L).status("PENDING").build();
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(5L, "UNKNOWN"))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_INVALID_STATUS);

        verify(orderRepository, never()).save(order);
    }

    private CheckoutRequest checkoutRequest(String paymentMethod) {
        CheckoutRequest request = new CheckoutRequest();
        request.setPaymentMethod(paymentMethod);
        request.setShippingAddress("123 Main Street");
        return request;
    }
}
