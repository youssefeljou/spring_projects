package com.example.book.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.book.dto.AutherDto;
import com.example.book.entity.Auther;

@Mapper
public interface AutherMapper {

	
	/*
	 * @Mapping(source = "fullname" , target = "name")
	 * used if there an variable with different nammes in the two classes
	 *  
	 *  @Mapping used to make array of mapping
	 *  
	 *  @Mappings({
		@Mapping(source = "id" , target = "id"),
		@Mapping(target = "name" , expression = "java(LocaleContextHolder.getLocale().getLanguage() ==\"ar\" ? entity.getFullName() :  entity.getFullName())"),
		@Mapping(target = "ipAddress" , defaultValue = "192.135.125.2"),
	})
	 *  
	 */
	//map from entity to dto
	AutherDto mapToDto(Auther entity);
	//map from dto to entity
	
	/*
	 * @Mapping( target = "name" , source = "fullname" )
	 * used if there an variable with different nammes in the two classes 
	 * 
	 */
	Auther mapToEntity (AutherDto dto);
}
