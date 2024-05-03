package com.profitsoft.restapi.repository;

import com.profitsoft.restapi.dto.author.AuthorDto;
import com.profitsoft.restapi.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("""
            select new com.profitsoft.restapi.dto.author.AuthorDto(a.id, a.name, a.birthYear, a.country)
            from Author a
            """)
    List<AuthorDto> findAllAuthors();

    Optional<Author> findByName(String name);
}
