package com.example.LTHD.service.impl;

import com.example.LTHD.dto.request.ProductCreateRequest;
import com.example.LTHD.dto.request.ProductUpdateRequest;
import com.example.LTHD.dto.response.ProductPageResponse;
import com.example.LTHD.dto.response.ProductResponse;
import com.example.LTHD.entity.Product;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.ProductMapper;
import com.example.LTHD.repository.ProductRepository;
import com.example.LTHD.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Phân trang cho danh sách sản phẩm
    @Override
    public ProductPageResponse getPageProducts(int page, int size, String sort) {
        if (page < 0) {
            throw new IllegalArgumentException("Page không được nhỏ hơn 0");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("Size phải lớn hơn 0");
        }

        Sort.Direction direction;

        if ("asc".equalsIgnoreCase(sort)) {
            direction = Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(sort)) {
            direction = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException("Sort chỉ nhận asc hoặc desc");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, "price"));

        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponse> products = productPage.getContent()
                .stream()
                .map(productMapper::toProductResponse)
                .toList();

        return new ProductPageResponse(
                products,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages());
    }

    // Lấy danh sách sản phẩm
    @Cacheable(value = "products")
    @Override
    public List<ProductResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    // Lấy sản phẩm theo ID
    @Override
    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        return productMapper.toProductResponse(product);
    }

    // Thêm sản phẩm
    @CacheEvict(value = { "products", "productById" }, allEntries = true)
    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {

        Product product = productMapper.toProduct(request);

        Product savedProduct = productRepository.save(product);

        return productMapper.toProductResponse(savedProduct);
    }

    // Cập nhật sản phẩm
    @CacheEvict(value = { "products", "productById" }, allEntries = true)
    @Override
    @Transactional
    public ProductResponse updateProduct(Integer id, ProductUpdateRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getVersion().equals(request.getVersion())) {
            throw new AppException(ErrorCode.PRODUCT_MODIFIED);
        }

        productMapper.updateProduct(product, request);

        Product updatedProduct = productRepository.save(product);

        return productMapper.toProductResponse(updatedProduct);
    }

    // Xóa sản phẩm
    @CacheEvict(value = { "products", "productById" }, allEntries = true)
    @Override
    public void deleteProduct(Integer id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
    }
}