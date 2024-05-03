package com.profitsoft.restapi.mapper;

import com.profitsoft.restapi.dto.author.AuthorDto;
import com.profitsoft.restapi.entity.Author;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AuthorMapper {

    AuthorDto toDto(Author author);

    Author toEntity(AuthorDto author);

    @Mapping(target = "id", ignore = true)
    void mergeToAuthorEntity(AuthorDto authorDto, @MappingTarget Author author);
}
