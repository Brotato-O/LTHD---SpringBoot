package com.example.LTHD.service;

import com.example.LTHD.dto.request.ProductCreateRequest;
import com.example.LTHD.dto.request.ProductUpdateRequest;
import com.example.LTHD.dto.response.ProductResponse;
import com.example.LTHD.entity.Product;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.ProductMapper;
import com.example.LTHD.repository.ProductRepository;
import com.example.LTHD.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_shouldMapSaveAndReturnProductResponse() {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Laptop");
        request.setPrice(new BigDecimal("1200.00"));
        request.setStock(5);

        Product product = Product.builder()
                .id(10)
                .name("Laptop")
                .price(new BigDecimal("1200.00"))
                .stock(5)
                .build();

        ProductResponse expectedResponse = ProductResponse.builder()
                .id(10)
                .name("Laptop")
                .price(new BigDecimal("1200.00"))
                .stock(5)
                .build();

        when(productMapper.toProduct(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toProductResponse(product)).thenReturn(expectedResponse);

        ProductResponse result = productService.createProduct(request);

        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getPrice()).isEqualByComparingTo("1200.00");
        assertThat(result.getStock()).isEqualTo(5);
        verify(productMapper).toProduct(request);
        verify(productRepository).save(product);
        verify(productMapper).toProductResponse(product);
    }

    @Test
    void getProducts_shouldReturnMappedResponses() {
        Product product1 = Product.builder().id(1).name("Laptop").price(new BigDecimal("1200.00")).stock(5).build();
        Product product2 = Product.builder().id(2).name("Mouse").price(new BigDecimal("50.00")).stock(20).build();

        ProductResponse response1 = ProductResponse.builder().id(1).name("Laptop").price(new BigDecimal("1200.00")).stock(5).build();
        ProductResponse response2 = ProductResponse.builder().id(2).name("Mouse").price(new BigDecimal("50.00")).stock(20).build();

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));
        when(productMapper.toProductResponse(product1)).thenReturn(response1);
        when(productMapper.toProductResponse(product2)).thenReturn(response2);

        List<ProductResponse> result = productService.getProducts();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProductResponse::getName).containsExactly("Laptop", "Mouse");
        assertThat(result).extracting(ProductResponse::getPrice)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(new BigDecimal("1200.00"), new BigDecimal("50.00"));
        verify(productMapper, times(2)).toProductResponse(any(Product.class));
    }

    @Test
    void getProductById_shouldRejectMissingProduct() {
        when(productRepository.findById(10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(10))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void updateProduct_shouldRejectMissingProduct() {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setVersion(3L);

        when(productRepository.findById(10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(10, request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

        verify(productRepository, never()).save(any(Product.class));
        verify(productMapper, never()).updateProduct(any(Product.class), any(ProductUpdateRequest.class));
    }

    @Test
    void updateProduct_shouldRejectStaleVersion() {
        Product product = Product.builder().id(10).version(3L).build();
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setVersion(2L);

        when(productRepository.findById(10)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateProduct(10, request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PRODUCT_MODIFIED);

        verify(productRepository, never()).save(any(Product.class));
        verify(productMapper, never()).updateProduct(any(Product.class), any(ProductUpdateRequest.class));
    }

    @Test
    void updateProduct_shouldUpdateWhenVersionMatches() {
        Product product = Product.builder().id(10).version(3L).build();
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setVersion(3L);

        ProductResponse expectedResponse = ProductResponse.builder()
                .id(10)
                .name("Laptop")
                .price(new BigDecimal("1200.00"))
                .stock(5)
                .version(3L)
                .build();

        when(productRepository.findById(10)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toProductResponse(product)).thenReturn(expectedResponse);

        ProductResponse result = productService.updateProduct(10, request);

        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getVersion()).isEqualTo(3L);
        verify(productMapper).updateProduct(product, request);
        verify(productRepository).save(product);
        verify(productMapper).toProductResponse(product);
    }

    @Test
    void deleteProduct_shouldRejectMissingProduct() {
        when(productRepository.findById(10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(10))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

        verify(productRepository, never()).delete(any(Product.class));
    }
}
