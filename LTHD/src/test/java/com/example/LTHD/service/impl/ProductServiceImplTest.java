package com.example.LTHD.service.impl;

import com.example.LTHD.dto.request.ProductCreateRequest;
import com.example.LTHD.dto.request.ProductUpdateRequest;
import com.example.LTHD.dto.response.ProductResponse;
import com.example.LTHD.entity.Product;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.ProductMapper;
import com.example.LTHD.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_ValidRequest_ShouldReturnResponse() {
        ProductCreateRequest request = new ProductCreateRequest();
        Product product = new Product();
        ProductResponse response = new ProductResponse();

        when(productMapper.toProduct(any())).thenReturn(product);
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toProductResponse(any())).thenReturn(response);

        ProductResponse result = productService.createProduct(request);
        assertNotNull(result);
    }

    @Test
    void getProductById_NotFound_ShouldThrowException() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> productService.getProductById(1));
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateProduct_VersionMismatch_ShouldThrowException() {
        // Dàn cảnh: Sản phẩm trong DB có version là 1
        Product product = new Product();
        product.setVersion(1L);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        // Request gửi lên có version là 2 (bị lệch)
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setVersion(2L);

        AppException exception = assertThrows(AppException.class,
                () -> productService.updateProduct(1, request));

        // Kỳ vọng ném ra lỗi PRODUCT_MODIFIED
        assertEquals(ErrorCode.PRODUCT_MODIFIED, exception.getErrorCode());
    }
}