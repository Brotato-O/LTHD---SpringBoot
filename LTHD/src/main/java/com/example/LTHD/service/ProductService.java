package com.example.LTHD.service;

import com.example.LTHD.dto.request.ProductCreateRequest;
import com.example.LTHD.dto.request.ProductUpdateRequest;
import com.example.LTHD.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getProducts();

    ProductResponse getProductById(Integer id);

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse updateProduct(Integer id, ProductUpdateRequest request);

    void deleteProduct(Integer id);
}