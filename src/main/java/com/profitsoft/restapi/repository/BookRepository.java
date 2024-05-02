package com.profitsoft.restapi.repository;

import com.profitsoft.restapi.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Override
    @EntityGraph(attributePaths = {"author"})
    Page<Book> findAll(Specification<Book> specification, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"author"})
    List<Book> findAll(Specification<Book> specification, Sort sort);
}
