
package com.example.LTHD.controller;

import com.example.LTHD.dto.request.ProductCreateRequest;
import com.example.LTHD.dto.request.ProductUpdateRequest;
import com.example.LTHD.dto.response.ApiResponse;
import com.example.LTHD.dto.response.ProductResponse;
import com.example.LTHD.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Lấy danh sách sản phẩm
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {

        List<ProductResponse> products = productService.getProducts();

        return new ApiResponse<>(
                200,
                "Lấy danh sách sản phẩm thành công",
                products);
    }

    // Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(
            @PathVariable Integer id) {

        ProductResponse product = productService.getProductById(id);

        return new ApiResponse<>(
                200,
                "Lấy thông tin sản phẩm thành công",
                product);
    }

    // Thêm sản phẩm
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {

        ProductResponse product = productService.createProduct(request);

        ApiResponse<ProductResponse> response = new ApiResponse<>(
                201,
                "Thêm sản phẩm thành công",
                product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductUpdateRequest request) {

        ProductResponse product = productService.updateProduct(id, request);

        return new ApiResponse<>(
                200,
                "Cập nhật sản phẩm thành công",
                product);
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(
            @PathVariable Integer id) {

        productService.deleteProduct(id);

        return new ApiResponse<>(
                200,
                "Xóa sản phẩm thành công",
                null);
    }
}
