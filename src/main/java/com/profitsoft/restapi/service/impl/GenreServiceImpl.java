package com.profitsoft.restapi.service.impl;

import com.profitsoft.restapi.dto.genre.GenreDto;
import com.profitsoft.restapi.mapper.GenreMapper;
import com.profitsoft.restapi.repository.GenreRepository;
import com.profitsoft.restapi.service.GenreService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    GenreRepository genreRepository;
    GenreMapper genreMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> findAll() {
        return genreRepository.findAll()
                .stream()
                .map(genreMapper::toDto)
                .toList();
    }
}
