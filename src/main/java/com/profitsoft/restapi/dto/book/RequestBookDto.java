package com.profitsoft.restapi.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RequestBookDto {

    @NotBlank(message = "title should not be blank")
    String title;

    @NotNull(message = "yearPublished should not be null")
    Integer yearPublished;

    @NotBlank(message = "publicationHouse should not be blank")
    String publicationHouse;

    @NotBlank(message = "genre should not be blank")
    String genre;

    @NotNull(message = "circulation should not be null")
    @Min(value = 100, message = "circulation value should be more than 100")
    Integer circulation;

    @NotNull(message = "pageAmount should not be null")
    @Min(value = 10, message = "pageAmount value should be more than 10")
    Integer pageAmount;

    @NotNull(message = "book should have the author id")
    Long authorId;
}