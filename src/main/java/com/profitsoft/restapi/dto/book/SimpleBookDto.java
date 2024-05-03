package com.profitsoft.restapi.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.profitsoft.restapi.dto.author.SimpleAuthorDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class SimpleBookDto {

    Long id;

    String title;

    String genre;

    String publicationHouse;

    @EqualsAndHashCode.Exclude
    @JsonProperty("author")
    SimpleAuthorDto simpleAuthorDto;
}
