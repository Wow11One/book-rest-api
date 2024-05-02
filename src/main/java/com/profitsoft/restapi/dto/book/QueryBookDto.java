package com.profitsoft.restapi.dto.book;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
public class QueryBookDto {

    @JsonUnwrapped
    FilterDto filterDto;

    @NotNull(message = "page can't be null")
    @Positive
    Integer page;

    @NotNull(message = "size can't be null")
    @Positive
    Integer size;

    public Long getAuthorId() {
        return filterDto.getAuthorId();
    }

    public String getGenre() {
        return filterDto.getGenre();
    }

    public String getPublicationHouse() {
        return filterDto.getPublicationHouse();
    }
}
