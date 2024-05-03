package com.profitsoft.restapi.dto.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
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
}
