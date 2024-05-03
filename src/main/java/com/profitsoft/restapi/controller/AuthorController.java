package com.profitsoft.restapi.controller;

import com.profitsoft.restapi.dto.author.AuthorDto;
import com.profitsoft.restapi.service.AuthorService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthorController {

    AuthorService authorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDto create(@Valid @RequestBody AuthorDto authorDto) {
        return authorService.create(authorDto);
    }

    @GetMapping
    public List<AuthorDto> findAll() {
        return authorService.findAll();
    }

    @GetMapping("/{id}")
    public AuthorDto findById(@PathVariable Long id) {
        return authorService.findOneById(id);
    }

    @PutMapping("/{id}")
    public AuthorDto update(@Valid @RequestBody AuthorDto authorDto, @PathVariable Long id) {
        return authorService.update(authorDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        authorService.delete(id);
    }
}
