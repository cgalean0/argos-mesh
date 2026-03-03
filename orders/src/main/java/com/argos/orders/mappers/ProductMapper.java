package com.argos.orders.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.argos.orders.dto.ProductRequest;
import com.argos.orders.dto.ProductResponse;
import com.argos.orders.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toDTO(Product entity);

    @Mapping(target = "productID", ignore = true)
    Product toEntity(ProductRequest dto);

    @Mapping(target = "productID", ignore = true)
    void updateEntityFromDto(ProductRequest dto, @MappingTarget Product entity);
}
