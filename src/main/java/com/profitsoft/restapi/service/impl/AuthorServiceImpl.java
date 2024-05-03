package com.profitsoft.restapi.service.impl;

import com.profitsoft.restapi.dto.author.AuthorDto;
import com.profitsoft.restapi.entity.Author;
import com.profitsoft.restapi.mapper.AuthorMapper;
import com.profitsoft.restapi.repository.AuthorRepository;
import com.profitsoft.restapi.service.AuthorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    AuthorRepository authorRepository;
    AuthorMapper authorMapper;

    @Override
    @Transactional
    public AuthorDto create(AuthorDto authorDto) {
        checkUniqueNameConstraint(authorDto.getName(), null);

        Author author = authorMapper.toEntity(authorDto);
        authorRepository.save(author);
        authorDto.setId(author.getId());

        return authorDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDto> findAll() {
        return authorRepository.findAllAuthors();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorDto findOneById(Long id) {
        Author author = findEntityById(id);

        return authorMapper.toDto(author);
    }

    @Override
    @Transactional
    public AuthorDto update(AuthorDto authorDto, Long id) {
        checkUniqueNameConstraint(authorDto.getName(), id);

        Author author = findEntityById(id);
        authorMapper.mergeToAuthorEntity(authorDto, author);
        authorRepository.save(author);
        authorDto.setId(id);

        return authorDto;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        authorRepository.deleteById(id);
    }

    private void checkUniqueNameConstraint(String name, Long id) {
        Optional<Author> authorNameCheck = authorRepository.findByName(name);
        authorNameCheck.ifPresent(author -> {
            if (!Objects.equals(author.getId(), id)) {
                throw new IllegalArgumentException("Author with such name already exists");
            }
        });
    }

    private Author findEntityById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("no author with such id exists"));
    }
}
