package com.profitsoft.restapi.service;

import com.profitsoft.restapi.dto.author.AuthorDto;

import java.util.List;

public interface AuthorService {

    AuthorDto create(AuthorDto authorDto);

    List<AuthorDto> findAll();

    AuthorDto findOneById(Long id);

    AuthorDto update (AuthorDto authorDto, Long id);

    void delete(Long id);
}
