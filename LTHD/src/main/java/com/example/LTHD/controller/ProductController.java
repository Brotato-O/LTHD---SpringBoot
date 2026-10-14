package com.example.LTHD.controller;

import com.example.LTHD.dto.request.ProductCreateRequest;
import com.example.LTHD.dto.request.ProductUpdateRequest;
import com.example.LTHD.dto.response.ProductResponse;
import com.example.LTHD.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Lấy danh sách sản phẩm
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getProducts();
    }

    // Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    // Thêm sản phẩm
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @Valid @RequestBody ProductCreateRequest request) {

        return productService.createProduct(request);
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductUpdateRequest request) {

        return productService.updateProduct(id, request);
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
    }
}