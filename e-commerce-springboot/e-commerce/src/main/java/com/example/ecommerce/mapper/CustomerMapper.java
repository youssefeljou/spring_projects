package com.example.ecommerce.mapper;


import com.example.ecommerce.dto.CustomerDto;
import com.example.ecommerce.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDto toDto(Customer customer);   // ✅ distinct name

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerDto dto);     // ✅ distinct name

}



