package com.profitsoft.restapi.mapper;

import com.profitsoft.restapi.dto.book.ResponseBookDto;
import com.profitsoft.restapi.dto.book.RequestBookDto;
import com.profitsoft.restapi.dto.book.SimpleBookDto;
import com.profitsoft.restapi.entity.Author;
import com.profitsoft.restapi.entity.Book;
import com.profitsoft.restapi.repository.AuthorRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public abstract class BookMapper {

    @Autowired
    AuthorRepository authorRepository;

    @Mapping(target = "author", source = "authorId", qualifiedByName = "mapAuthor")
    public abstract Book toEntity(RequestBookDto requestBookDto);

    @Mapping(target = "simpleAuthorDto.id", source = "author.id")
    @Mapping(target = "simpleAuthorDto.name", source = "author.name")
    public abstract ResponseBookDto toResponseDto(Book book);

    @Mapping(target = "simpleAuthorDto.id", source = "author.id")
    @Mapping(target = "simpleAuthorDto.name", source = "author.name")
    public abstract SimpleBookDto toSimpleDto(Book book);

    @Mapping(target = "author", source = "authorId", qualifiedByName = "mapAuthor")
    public abstract void mergeToBookEntity(RequestBookDto requestBookDto,
                                           @MappingTarget Book book);

    @Named("mapAuthor")
    public Author mapAuthor(Long authorId) {
        return this.authorRepository.findById(authorId).orElseThrow(
                () -> new NoSuchElementException("author with such id does not exist")
        );
    }
}
