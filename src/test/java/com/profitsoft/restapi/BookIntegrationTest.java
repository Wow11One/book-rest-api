package com.profitsoft.restapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profitsoft.restapi.dto.book.FilterDto;
import com.profitsoft.restapi.dto.book.QueryBookDto;
import com.profitsoft.restapi.dto.book.RequestBookDto;
import com.profitsoft.restapi.dto.book.SimpleBookDto;
import com.profitsoft.restapi.entity.Author;
import com.profitsoft.restapi.entity.Book;
import com.profitsoft.restapi.mapper.BookMapper;
import com.profitsoft.restapi.repository.AuthorRepository;
import com.profitsoft.restapi.repository.BookRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.jdbc.Sql;

import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16-alpine");

    MockMvc mockMvc;
    BookRepository bookRepository;
    BookMapper bookMapper;
    ObjectMapper objectMapper;
    AuthorRepository authorRepository;
    Author author;

    @Autowired
    public BookIntegrationTest(MockMvc mockMvc,
                               BookRepository bookRepository,
                               BookMapper bookMapper,
                               AuthorRepository authorRepository,
                               ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.authorRepository = authorRepository;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void init() {
        author = new Author(null, "test author", 1999, "ukraine", null);
        authorRepository.save(author);
    }

    @AfterEach
    public void tearDown() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    // POST /api/books tests
    @Test
    public void testThatValidBookIsSavedInDbAfterPostRequest() throws Exception {
        RequestBookDto requestBookDto = new RequestBookDto(
                "new book",
                2024,
                "folio",
                "fantasy",
                10000,
                123,
                author.getId()
        );

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookDto))
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andReturn();

        Long newBookId = objectMapper.readTree(mvcResult.getResponse().getContentAsString())
                .get("id")
                .asLong();

        Optional<Book> expectedBook = bookRepository.findById(newBookId);

        assertThat(expectedBook.isPresent()).isTrue();

        Book book = expectedBook.get();

        assertThat(book.getTitle()).isEqualTo(requestBookDto.getTitle());
        assertThat(book.getYearPublished()).isEqualTo(requestBookDto.getYearPublished());
        assertThat(book.getPublicationHouse()).isEqualTo(requestBookDto.getPublicationHouse());
        assertThat(book.getGenre()).isEqualTo(requestBookDto.getGenre());
        assertThat(book.getCirculation()).isEqualTo(requestBookDto.getCirculation());
        assertThat(book.getPageAmount()).isEqualTo(requestBookDto.getPageAmount());
        assertThat(book.getAuthor().getId()).isEqualTo(requestBookDto.getAuthorId());
    }

    @Test
    public void testThatRequestOfInvalidBookIsNotSuccessful() throws Exception {
        RequestBookDto requestBookDto = new RequestBookDto(
                "new book",
                2024,
                "",
                "",
                10000,
                123,
                author.getId()
        );

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookDto))
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testThatRequestOfBookWithNonExistentAuthorIsNotSuccessful() throws Exception {
        RequestBookDto requestBookDto = new RequestBookDto(
                "new book",
                2024,
                "folio",
                "fantasy",
                10000,
                123,
                -666L
        );

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookDto))
        ).andExpectAll(
                MockMvcResultMatchers.status().isNotFound()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                        .value("author with such id does not exist")
        );
    }

    // GET /api/books/:id tests
    @Test
    public void testThatIfBookIsStoredInDbThenRequestIsSuccessful() throws Exception {
        Book book = Book.builder()
                .title("new")
                .yearPublished(1922)
                .publicationHouse("house")
                .genre("genre")
                .circulation(12321)
                .pageAmount(231)
                .author(author)
                .build();

        bookRepository.save(book);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/books/" + book.getId())
        ).andExpect(
                MockMvcResultMatchers.status().is2xxSuccessful()
        ).andExpectAll(
                MockMvcResultMatchers.jsonPath("$.id")
                        .value(book.getId()),
                MockMvcResultMatchers.jsonPath("$.title")
                        .value(book.getTitle()),
                MockMvcResultMatchers.jsonPath("$.yearPublished")
                        .value(book.getYearPublished()),
                MockMvcResultMatchers.jsonPath("$.publicationHouse")
                        .value(book.getPublicationHouse()),
                MockMvcResultMatchers.jsonPath("$.genre")
                        .value(book.getGenre()),
                MockMvcResultMatchers.jsonPath("$.circulation")
                        .value(book.getCirculation()),
                MockMvcResultMatchers.jsonPath("$.pageAmount")
                        .value(book.getPageAmount()),
                MockMvcResultMatchers.jsonPath("$.author.id")
                        .value(book.getAuthor().getId())
        );
    }

    @Test
    public void testThatRequestWithNonExistentIdIsFailed() throws Exception {
        Long nonExistentId = -3L;

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/books/" + nonExistentId)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                        .value("book with such id does not exist")
        );
    }

    // POST /api/books/_search tests
    @ParameterizedTest
    @MethodSource("filterSource")
    @SqlGroup(value = {
            @Sql("/sql/create_teachers.sql"), // init teachers data
            @Sql("/sql/books_test_data.sql")  // data with 15 books for testing
    })
    public void testThatListRequestReturnsCorrectData(FilterDto filterDto) throws Exception {
        Integer size = 10;
        Integer page = 1;

        List<SimpleBookDto> expectedBookList = bookRepository.findAll()
                .stream()
                .filter(book -> filterBuilder(book, filterDto))
                .limit(size)
                .map(bookMapper::toSimpleDto)
                .toList();

        QueryBookDto queryBookDto = QueryBookDto.builder()
                .page(page)
                .size(size)
                .filterDto(filterDto)
                .build();

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/books/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryBookDto))
        ).andExpect(
                MockMvcResultMatchers.status().is2xxSuccessful()
        ).andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        List<SimpleBookDto> actualBookList = objectMapper.readValue(
                jsonNode.get("list").toString(),
                new TypeReference<>() {
                }
        );

        assertThat(actualBookList).isNotEmpty();
        assertThat(expectedBookList).isNotEmpty();
        assertThat(actualBookList).hasSize(expectedBookList.size());
        assertThat(actualBookList).containsExactlyElementsOf(expectedBookList);
    }

    @Test
    public void testThatListRequestWithInvalidBodyFails() throws Exception {
        Integer size = -15; // dto has validation, so response should return bad request status
        Integer page = -1;

        // using pagination params but without filters
        QueryBookDto queryBookDto = QueryBookDto.builder()
                .page(page)
                .size(size)
                .build();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/books/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryBookDto))
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    // POST /api/books/_report tests
    @ParameterizedTest
    @MethodSource("filterSource")
    @SqlGroup(value = {
            @Sql("/sql/create_teachers.sql"),
            @Sql("/sql/books_test_data.sql")
    })
    public void testThatReportRequestReturnsCorrectExcel(FilterDto filterDto) throws Exception {
        // I map entities to ids because it makes the process of testing easier
        List<Long> expectedBookIdsList = bookRepository.findAll()
                .stream()
                .filter(book -> filterBuilder(book, filterDto))
                .map(Book::getId)
                .toList();

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/books/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto))
        ).andExpectAll(
                MockMvcResultMatchers.status().is2xxSuccessful(),
                MockMvcResultMatchers
                        .header()
                        .string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
        ).andReturn();

        // read the file from response body using apache poi
        List<Long> actualBookIdsList = getBookIdsFromExcel(mvcResult.getResponse().getContentAsByteArray());

        assertThat(actualBookIdsList).isNotEmpty();
        assertThat(expectedBookIdsList).isNotEmpty();
        assertThat(actualBookIdsList).hasSize(expectedBookIdsList.size());
        assertThat(actualBookIdsList).containsExactlyElementsOf(expectedBookIdsList);
    }

    @Test
    @SqlGroup(value = {
            @Sql("/sql/create_teachers.sql"),
            @Sql("/sql/books_test_data.sql")
    })
    public void testThatReportRequestWithNotCorrectFilterValueReturnsEmptyFile()
            throws Exception {
        FilterDto filterDto = FilterDto.builder()
                .authorId(-333L) // set author id that does not exist
                .build();
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/books/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto))
        ).andExpectAll(
                MockMvcResultMatchers.status().is2xxSuccessful(),
                MockMvcResultMatchers
                        .header()
                        .string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
        ).andReturn();

        // read the file from response body using apache poi
        List<Long> actualBookIdsList = getBookIdsFromExcel(mvcResult.getResponse().getContentAsByteArray());

        assertThat(actualBookIdsList).isEmpty();
    }

    // POST /api/books/upload tests
    @ParameterizedTest
    @MethodSource("uploadFilesSource")
    @Sql("/sql/create_teachers.sql")
    public void testThatValidFileIsSuccessfullyUploaded(File jsonFile,
                                                        Integer expectedSuccessfulCount,
                                                        Integer expectedFailedCount) throws Exception {
        Long initialBookAmount = bookRepository.count();
        MockPart mockPart = new MockPart(
                "file",
                jsonFile.getName(),
                Files.readAllBytes(jsonFile.toPath()),
                MediaType.APPLICATION_JSON
        );

        mockMvc.perform(multipart("/api/books/upload").part(mockPart))
                .andExpect(
                        MockMvcResultMatchers.status().isOk()
                ).andExpectAll(
                        MockMvcResultMatchers.jsonPath("$.successful").value(expectedSuccessfulCount),
                        MockMvcResultMatchers.jsonPath("$.failed").value(expectedFailedCount)
                );

        Long bookAmountAfterUpload = bookRepository.count();

        assertThat(bookAmountAfterUpload).isGreaterThan(initialBookAmount);
        assertThat(bookAmountAfterUpload - expectedSuccessfulCount).isEqualTo(initialBookAmount);
    }

    @Test
    public void testThatInvalidFileIsNotUploaded() throws Exception {
        String folderPath = "src/test/resources/test-upload-json-data/";
        File invalidFile = new File(folderPath + "not-correct-format.json");
        MockPart mockPart = new MockPart(
                "file",
                invalidFile.getName(),
                Files.readAllBytes(invalidFile.toPath()),
                MediaType.APPLICATION_JSON
        );

        mockMvc.perform(multipart("/api/books/upload").part(mockPart))
                .andExpect(
                        MockMvcResultMatchers.status().isBadRequest()
                );
    }

    // PUT /api/books/:id tests
    @Test
    public void testThatPutRequestUpdatesUserInDb() throws Exception {
        Book newBook = Book.builder()
                .title("new book")
                .yearPublished(1923)
                .publicationHouse("house")
                .genre("some genre")
                .pageAmount(323)
                .circulation(323)
                .author(author)
                .build();

        bookRepository.save(newBook);
        RequestBookDto requestBookDto = RequestBookDto.builder()
                .title("new title")
                .publicationHouse("new house")
                .yearPublished(1923)
                .genre("new genre")
                .pageAmount(1293)
                .circulation(666)
                .authorId(newBook.getAuthor().getId())
                .build();

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/books/" + newBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookDto))
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

        Book updatedBook = bookRepository.findById(newBook.getId()).orElseThrow();

        assertThat(updatedBook.getTitle()).isEqualTo(requestBookDto.getTitle());
        assertThat(updatedBook.getYearPublished()).isEqualTo(requestBookDto.getYearPublished());
        assertThat(updatedBook.getPublicationHouse()).isEqualTo(requestBookDto.getPublicationHouse());
        assertThat(updatedBook.getGenre()).isEqualTo(requestBookDto.getGenre());
        assertThat(updatedBook.getPageAmount()).isEqualTo(requestBookDto.getPageAmount());
        assertThat(updatedBook.getCirculation()).isEqualTo(requestBookDto.getCirculation());
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(requestBookDto.getAuthorId());
    }

    @Test
    public void testThatInvalidRequestFails() throws Exception {
        Book newBook = Book.builder()
                .title("new book")
                .yearPublished(1923)
                .publicationHouse("house")
                .genre("some genre")
                .pageAmount(323)
                .circulation(323)
                .author(author)
                .build();

        bookRepository.save(newBook);

        RequestBookDto requestBookDto = RequestBookDto.builder()
                .title("") // invalid title
                .publicationHouse(null) // invalid publication house
                .yearPublished(1923)
                .genre("new genre")
                .pageAmount(1293)
                .circulation(666)
                .authorId(author.getId())
                .build();

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/books/" + newBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookDto))
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testThatRequestForNonExistentBookFails() throws Exception {
        RequestBookDto requestBookDto = RequestBookDto.builder()
                .title("title")
                .publicationHouse("house")
                .yearPublished(1923)
                .genre("new genre")
                .pageAmount(1293)
                .circulation(666)
                .authorId(author.getId())
                .build();

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/books/" + -555L) //random nonexistent id
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookDto))
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    // DELETE /api/books/:id tests
    @Test
    public void testThatAfterDeleteUserIsNotStoredInDb() throws Exception {
        Book book = Book.builder()
                .title("new book")
                .yearPublished(1923)
                .publicationHouse("house")
                .genre("some genre")
                .pageAmount(323)
                .circulation(323)
                .author(author)
                .build();

        bookRepository.save(book);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/books/" + book.getId()) //random nonexistent id
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/books/" + book.getId()) //random nonexistent id
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/books/" + book.getId()) //random nonexistent id
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );

        Optional<Book> deletedBook = bookRepository.findById(book.getId());
        assertThat(deletedBook.isEmpty()).isTrue();
    }

    private List<Long> getBookIdsFromExcel(byte[] bytes) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(
                new ByteArrayInputStream(bytes)
        );
        HSSFSheet sheet = workbook.getSheet("Book info");
        List<Long> actualBookIdsList = new ArrayList<>();

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Iterator<Cell> iterator = sheet.getRow(i).cellIterator();
            Cell cell = iterator.next();
            actualBookIdsList.add((long) cell.getNumericCellValue());
        }
        return actualBookIdsList;
    }

    private boolean filterBuilder(Book book, FilterDto filterDto) {
        boolean result = true;

        if (filterDto.getAuthorId() != null) {
            result = Objects.equals(book.getAuthor().getId(), filterDto.getAuthorId());
        }
        if (filterDto.getGenre() != null) {
            result &= Objects.equals(book.getGenre(), filterDto.getGenre());
        }
        if (filterDto.getPublicationHouse() != null) {
            result &= result && Objects.equals(book.getPublicationHouse(), filterDto.getPublicationHouse());
        }

        return result;
    }

    private static Stream<Arguments> uploadFilesSource() {
        // I pass the files for upload with expected amounts of successfully saved files
        // and the failed ones
        String folderPath = "src/test/resources/test-upload-json-data/";

        return Stream.of(
                Arguments.of(new File(folderPath + "10-0.json"), 10, 0),
                Arguments.of(new File(folderPath + "8-2.json"), 8, 2),
                Arguments.of(new File(folderPath + "7-3.json"), 7, 3)
        );
    }

    // generate filters for POST /api/books/_list request with 1, 2 and 3 parameters
    private static Stream<FilterDto> filterSource() {
        return Stream.of(
                FilterDto.builder()
                        .build(),
                FilterDto.builder()
                        .authorId(1L)
                        .build(),
                FilterDto.builder()
                        .genre("fantasy")
                        .build(),
                FilterDto.builder()
                        .publicationHouse("folio")
                        .build(),
                FilterDto.builder()
                        .publicationHouse("folio")
                        .genre("fantasy")
                        .build(),
                FilterDto.builder()
                        .publicationHouse("folio")
                        .authorId(1L)
                        .build(),
                FilterDto.builder()
                        .genre("fantasy")
                        .authorId(1L)
                        .build(),
                FilterDto.builder()
                        .publicationHouse("folio")
                        .genre("fantasy")
                        .authorId(1L)
                        .build()
        );
    }
}
