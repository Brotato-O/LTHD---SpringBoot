package com.example.LTHD.mapper;

import com.example.LTHD.dto.request.ProductCreateRequest;
import com.example.LTHD.dto.request.ProductUpdateRequest;
import com.example.LTHD.dto.response.ProductResponse;
import com.example.LTHD.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // ProductCreateRequest -> Product
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toProduct(ProductCreateRequest request);

    // ProductUpdateRequest -> Product đang tồn tại
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProduct(
            @MappingTarget Product product,
            ProductUpdateRequest request);

    // Product -> ProductResponse
    ProductResponse toProductResponse(Product product);
}