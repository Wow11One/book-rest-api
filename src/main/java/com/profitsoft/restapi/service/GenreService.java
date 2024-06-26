package com.profitsoft.restapi.service;

import com.profitsoft.restapi.dto.genre.GenreDto;

import java.util.List;

public interface GenreService {

    List<GenreDto> findAll();
}
