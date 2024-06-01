package com.profitsoft.restapi.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.profitsoft.restapi.dto.author.SimpleAuthorDto;
import com.profitsoft.restapi.dto.genre.GenreDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
public class ResponseBookDto {

    Long id;

    String title;

    Integer yearPublished;

    String publicationHouse;

    Integer circulation;

    Integer pageAmount;

    String image;

    @JsonProperty("genre")
    GenreDto genreDto;

    @JsonProperty("author")
    SimpleAuthorDto simpleAuthorDto;
}