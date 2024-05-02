package com.profitsoft.restapi.dto.book;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@Getter
@Setter
public class ListBookDto {

    Integer totalPages;
    List<SimpleBookDto> list;
}
