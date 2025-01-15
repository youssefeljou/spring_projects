package com.example.education.mapper;

import com.example.education.dto.MasterDto;
import com.example.education.model.Master;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MasterMapper {

    MasterDto mapToDto(Master master);
    List<MasterDto> mapToDto(List<Master> masters);

    Master mapToEntity(MasterDto masterDto);
}
