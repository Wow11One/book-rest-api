package com.profitsoft.restapi.controller;

import com.profitsoft.restapi.dto.genre.GenreDto;
import com.profitsoft.restapi.service.GenreService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GenreController {

    GenreService genreService;

    @GetMapping
    public List<GenreDto> findAll() {
        return genreService.findAll();
    }
}
