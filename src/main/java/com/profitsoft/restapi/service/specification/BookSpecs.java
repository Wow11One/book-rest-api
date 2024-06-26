package com.profitsoft.restapi.service.specification;

import com.profitsoft.restapi.entity.Author_;
import com.profitsoft.restapi.entity.Book;
import com.profitsoft.restapi.entity.Book_;
import com.profitsoft.restapi.entity.Genre_;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public class BookSpecs {

    public static Specification<Book> equalsAuthorId(Long authorId) {
        return (root, query, builder) ->
                builder.equal(root.get(Book_.AUTHOR).get(Author_.ID), authorId);
    }

    public static Specification<Book> equalsGenreId(Long genreId) {
        return (root, query, builder) ->
                builder.equal(root.get(Book_.GENRE).get(Genre_.ID), genreId);
    }

    public static Specification<Book> equalsPublicationHouse(String publicationHouse) {
        return (root, query, builder) ->
                builder.equal(root.get(Book_.PUBLICATION_HOUSE), publicationHouse);
    }

    public static Specification<Book> buildQuery(List<Specification<Book>> specifications) {
        return Specification.allOf(specifications);
    }
}