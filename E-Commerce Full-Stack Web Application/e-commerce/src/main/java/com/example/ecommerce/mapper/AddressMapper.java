package com.example.ecommerce.mapper;


import com.example.ecommerce.dto.AddressDto;
import com.example.ecommerce.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    // Entity to DTO
    AddressDto toDto(Address address);

    // DTO to Entity
    Address toEntity(AddressDto addressDto);
}
