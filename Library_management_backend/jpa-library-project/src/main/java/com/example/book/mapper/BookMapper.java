package com.example.book.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.book.dto.AutherDto;
import com.example.book.dto.BookDto;
import com.example.book.entity.Auther;
import com.example.book.entity.Book;

@Mapper(uses = AutherMapper.class)
public interface BookMapper {

	/*
	 * @Mapping(source = "fullname" , target = "name") used if there an variable
	 * with different nammes in the two classes
	 * 
	 * @Mapping used to make array of mapping
	 * 
	 * @Mappings({
	 * 
	 * @Mapping(source = "id" , target = "id"),
	 * 
	 * @Mapping(target = "name" , expression =
	 * "java(LocaleContextHolder.getLocale().getLanguage() ==\"ar\" ? entity.getFullName() :  entity.getFullName())"
	 * ),
	 * 
	 * @Mapping(target = "ipAddress" , defaultValue = "192.135.125.2"), })
	 * 
	 */
	// map from entity to dto
	@Mapping(target = "auther", ignore = true)
	@Mapping(target = "autherName", source = "auther.name")
	@Mapping(target = "autherEmail", source = "auther.email")
	BookDto mapToDto(Book entity);
	// map from dto to entity

	/*
	 * @Mapping( target = "name" , source = "fullname" ) used if there an variable
	 * with different nammes in the two classes
	 * 
	 */
	@Mapping(source = "autherName", target = "auther.name")
	@Mapping(source = "autherEmail", target = "auther.email")
	Book mapToEntity(BookDto dto);
}
