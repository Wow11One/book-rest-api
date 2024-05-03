package com.profitsoft.restapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profitsoft.restapi.dto.author.AuthorDto;
import com.profitsoft.restapi.entity.Author;
import com.profitsoft.restapi.entity.Book;
import com.profitsoft.restapi.mapper.AuthorMapper;
import com.profitsoft.restapi.repository.AuthorRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Testcontainers
public class AuthorIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16-alpine");

    MockMvc mockMvc;
    AuthorRepository authorRepository;
    AuthorMapper authorMapper;
    ObjectMapper objectMapper;

    @Autowired
    public AuthorIntegrationTest(MockMvc mockMvc,
                                 AuthorRepository authorRepository,
                                 AuthorMapper authorMapper,
                                 ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setUp() {
        authorRepository.deleteAll();
    }

    // POST /api/authors tests
    @Test
    public void testThatValidAuthorIsSavedInDbSuccessfully() throws Exception {
        String authorName = "new author";
        Optional<Author> checkAuthor = authorRepository.findByName(authorName);

        assertThat(checkAuthor.isEmpty()).isTrue();

        AuthorDto authorDto = new AuthorDto();
        authorDto.setName(authorName);
        authorDto.setCountry("ukraine");
        authorDto.setBirthYear(1993);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto))
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Long id = objectMapper.readTree(responseBody).get("id").asLong();

        Author author = authorRepository.findById(id).orElseThrow();

        assertThat(author.getName()).isEqualTo(authorDto.getName());
        assertThat(author.getCountry()).isEqualTo(authorDto.getCountry());
        assertThat(author.getBirthYear()).isEqualTo(authorDto.getBirthYear());
    }

    @Test
    public void testThatInValidAuthorRequestFails() throws Exception {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setName(null); // invalid name value
        authorDto.setCountry(""); // invalid country value
        authorDto.setBirthYear(1993);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto))
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testThatAuthorWithExistingNameWillNotBeSaved() throws Exception {
        String authorName = "some name";
        Author author = new Author();
        author.setBirthYear(1900);
        author.setCountry("Ukraine");
        author.setName(authorName);

        authorRepository.save(author);

        AuthorDto authorDto = new AuthorDto();
        authorDto.setName(authorName); // using name that already was saved in db
        authorDto.setCountry("Ukraine"); // invalid country value
        authorDto.setBirthYear(1993);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto))
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                        .value("Author with such name already exists")
        );
    }

    // GET /api/authors tests
    @Test
    public void testThatGetRequestReturnsAllAuthors() throws Exception {
        List<Author> expectedAuthorList = List.of(
                new Author(null, "author1", 1996, "Ukraine", null),
                new Author(null, "author2", 1997, "Ukraine", null),
                new Author(null, "author3", 1997, "Ukraine", null)
        );

        authorRepository.saveAll(expectedAuthorList);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/authors")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        List<Author> actualAuthorList = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertThat(actualAuthorList).isNotEmpty();
        assertThat(actualAuthorList).hasSize(expectedAuthorList.size());
        assertThat(actualAuthorList).containsExactlyElementsOf(expectedAuthorList);
    }

    // GET /api/authors/:id tests
    @Test
    public void testThatGetRequestWithValidIdIsSuccessful() throws Exception {
        String authorName = "some name";
        Author author = new Author();
        author.setBirthYear(1900);
        author.setCountry("Ukraine");
        author.setName(authorName);

        authorRepository.save(author);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/authors/" + author.getId())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpectAll(
                MockMvcResultMatchers.jsonPath("$.id").value(author.getId()),
                MockMvcResultMatchers.jsonPath("$.name").value(author.getName()),
                MockMvcResultMatchers.jsonPath("$.country").value(author.getCountry()),
                MockMvcResultMatchers.jsonPath("$.birthYear").value(author.getBirthYear())
        );
    }

    @Test
    public void testThatGetRequestWithInvalidIdIsFails() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/authors/" + -555) // some random id
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    // PUT /api/authors/:id tests
    @Test
    public void testThatPutRequestWithValidFieldsUpdatesAuthor() throws Exception {
        Author author = new Author();
        author.setBirthYear(1900);
        author.setCountry("Ukraine");
        author.setName("author name");

        authorRepository.save(author);

        AuthorDto authorDto = new AuthorDto();
        authorDto.setName("new name");
        authorDto.setCountry("new country");
        authorDto.setBirthYear(1995);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/authors/" + author.getId()) // some random id
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto))
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

        Author updatedAuthor = authorRepository.findById(author.getId()).orElseThrow();

        assertThat(updatedAuthor.getName()).isEqualTo(authorDto.getName());
        assertThat(updatedAuthor.getCountry()).isEqualTo(authorDto.getCountry());
        assertThat(updatedAuthor.getBirthYear()).isEqualTo(authorDto.getBirthYear());
    }

    @Test
    public void testThatPutRequestWithExistingAuthorNameFails() throws Exception {
        String authorName = "author name";
        Author author = new Author();
        author.setBirthYear(1900);
        author.setCountry("Ukraine");
        author.setName(authorName);

        authorRepository.save(author);

        Author authorToBeUpdated = new Author();
        authorToBeUpdated.setBirthYear(1900);
        authorToBeUpdated.setCountry("Ukraine");
        authorToBeUpdated.setName("another author");

        authorRepository.save(authorToBeUpdated);

        AuthorDto authorDto = new AuthorDto();
        authorDto.setName(authorName); // using name that already was saved in db
        authorDto.setCountry("new country"); // invalid country value
        authorDto.setBirthYear(1995);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/authors/" + authorToBeUpdated.getId()) // some random id
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto))
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    // DELETE /api/authors/:id tests
    @Test
    public void testThatAfterDeleteAuthorIsNotStoredInDb() throws Exception {
        Author author = new Author();
        author.setBirthYear(1900);
        author.setCountry("Ukraine");
        author.setName("author name");

        authorRepository.save(author);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/authors/" + author.getId())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/authors/" + author.getId())
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/authors/" + author.getId())
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );

        Optional<Author> checkAuthor = authorRepository.findById(author.getId());

        assertThat(checkAuthor.isEmpty()).isTrue();
    }
}
