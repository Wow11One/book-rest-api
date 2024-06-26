package com.profitsoft.restapi.mapper;

import com.profitsoft.restapi.dto.genre.GenreDto;
import com.profitsoft.restapi.entity.Genre;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface GenreMapper {
    GenreDto toDto(Genre genre);
}
