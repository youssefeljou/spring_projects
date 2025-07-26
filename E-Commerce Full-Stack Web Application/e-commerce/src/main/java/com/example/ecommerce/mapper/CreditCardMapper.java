package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.CreditCardCreateDTO;
import com.example.ecommerce.dto.CreditCardViewDTO;
import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.util.CardUtil;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.YearMonth;

@Mapper(componentModel = "spring", imports = {YearMonth.class, CardUtil.class})
public interface CreditCardMapper {

    CreditCardMapper INSTANCE = Mappers.getMapper(CreditCardMapper.class);

    @Mapping(target = "id", ignore = true) //auto-generated
    @Mapping(target = "cardNumberEnc", ignore = true) //set after mapping
    @Mapping(target = "last4", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "expiration",
            source = "expiration",
            qualifiedByName = "mmYyToYearMonth")
    @Mapping(target = "customer", ignore = true) //Set in service
    CreditCard toEntity(CreditCardCreateDTO dto);


    CreditCardViewDTO toDto(CreditCard entity);

    @Named("mmYyToYearMonth")
    static YearMonth mmYyToYearMonth(String mmYy) {
        //dto.expiration() == "MM/YY"
        return YearMonth.parse("20" + mmYy.substring(3) + "-" + mmYy.substring(0, 2));
    }


    @AfterMapping
    default void enrich(@MappingTarget CreditCard card, CreditCardCreateDTO dto) {
        card.setPlainCardNumber(dto.cardNumber());
    }
}
