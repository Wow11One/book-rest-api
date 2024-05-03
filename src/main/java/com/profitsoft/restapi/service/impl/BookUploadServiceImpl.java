package com.profitsoft.restapi.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.profitsoft.restapi.dto.book.RequestBookDto;
import com.profitsoft.restapi.entity.Book;
import com.profitsoft.restapi.entity.Book_;
import com.profitsoft.restapi.exception.ApiException;
import com.profitsoft.restapi.exception.JsonFormatException;
import com.profitsoft.restapi.mapper.BookMapper;
import com.profitsoft.restapi.service.BookUploadService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The class that analyses certain json file.
 * The most important method here is 'analyse()'.
 * It parses json file by using jackson streaming api
 * object by object. In case file format is not correct
 * then JsonParseException or custom JsonFormatException
 * can occur. If required attribute is missing in object,
 * then no error occurs. Also, it is important to mention
 * that there is a 'SEARCH_LIMIT' field that limits the amount
 * of token for analysing in case the file format is broken.
 * This class was reused from previous practice task and
 * adapted to the new purpose.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class BookUploadServiceImpl implements BookUploadService {

    final List<Book> resultList;
    final Integer SEARCH_LIMIT;
    final ValidatorFactory factory;
    final String AUTHOR_ID = "authorId";
    final BookMapper bookMapper;
    JsonParser parser;
    @Getter
    Integer successfulCount;
    @Getter
    Integer failedCount;

    public BookUploadServiceImpl(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
        this.SEARCH_LIMIT = 5000;
        this.resultList = new ArrayList<>();
        this.successfulCount = 0;
        this.failedCount = 0;
        this.factory = Validation.buildDefaultValidatorFactory();
    }

    /**
     * Starts analysis of a file using jackson streaming api.
     * It checks whether first token of the file is the array start
     * and then stars a recursive helper method that handles parsing
     * of the array objects.
     *
     * @return a list of values of a specified attribute. It counts
     * all occurrences of attribute in file.
     */
    public List<Book> analyse(InputStream inputStream) {
        try {
            JsonFactory factory = new JsonFactory();
            parser = factory.createParser(inputStream);

            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new JsonFormatException(parser,
                        "incorrect json array: no start array token detected");
            }

            analyseHelp(0, new RequestBookDto());

            return resultList;
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new ApiException(exception.getMessage());
        }
    }

    /**
     * Helper method that recursively parse objects in the json array
     * from a file. This method also delegates json object fields
     * handling to other methods. The method stops if the current
     * token is the end of array or search limit is exceeded.
     *
     * @param countIterator counts how many iteration method does.
     *                      In case 'countIterator' equals to 'SEARCH_LIMIT',
     *                      method stops.
     * @throws IOException         when file format is not correct or does not exist.
     * @throws JsonFormatException In case file format is not correct.
     */
    private void analyseHelp(Integer countIterator, RequestBookDto requestBookDto) throws IOException {
        parser.nextToken();

        if (parser.currentToken() == JsonToken.END_ARRAY) {
            return;
        }
        if (parser.currentToken() != JsonToken.START_OBJECT ||
                Objects.equals(countIterator, SEARCH_LIMIT)) {
            throw new JsonFormatException(parser,
                    "not correct json format: object should start from start_object token");
        }

        parser.nextToken();

        while (parser.currentToken() != JsonToken.END_OBJECT) {
            if (parser.currentToken() != JsonToken.FIELD_NAME) {
                log.error(
                        "expected token {}, actual token {}",
                        JsonToken.FIELD_NAME,
                        parser.currentName() + " " + parser.currentToken()
                );
                throw new JsonFormatException(parser,
                        "not correct json format: expected token is not field");
            }
            handleAttributeValue(requestBookDto);
        }

        addNewBook(requestBookDto);
        analyseHelp(++countIterator, new RequestBookDto());
    }

    private void addNewBook(RequestBookDto requestBookDto) {
        try {
            if (isValidated(requestBookDto)) {
                Book book = bookMapper.toEntity(requestBookDto);
                resultList.add(book);
                this.successfulCount++;
            } else {
                this.failedCount++;
            }
        } catch (Exception exception) {
            this.failedCount++;
            log.error(exception.getMessage());
        }
    }

    private boolean isValidated(RequestBookDto requestBookDto) {
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<RequestBookDto>> violations = validator.validate(requestBookDto);

        if (!violations.isEmpty()) {
            violations.forEach(violation -> log.error(violation.getMessage()));
            return false;
        }

        return true;
    }

    /**
     * Handles values of a field that is specified by user.
     *
     * @throws IOException when file format is not correct or does not exist.
     */
    private void handleAttributeValue(RequestBookDto requestBookDto) throws IOException {
        parser.nextToken();

        switch (parser.getCurrentName()) {
            case Book_.TITLE:
                requestBookDto.setTitle(parser.getText());
                break;
            case Book_.YEAR_PUBLISHED:
                requestBookDto.setYearPublished(parser.getIntValue());
                break;
            case Book_.PUBLICATION_HOUSE:
                requestBookDto.setPublicationHouse(parser.getText());
                break;
            case Book_.GENRE:
                requestBookDto.setGenre(parser.getText());
                break;
            case Book_.CIRCULATION:
                requestBookDto.setCirculation(parser.getIntValue());
                break;
            case Book_.PAGE_AMOUNT:
                requestBookDto.setPageAmount(parser.getIntValue());
                break;
            case AUTHOR_ID:
                requestBookDto.setAuthorId(parser.getLongValue());
                break;
            default:
                log.warn("excessive field value {}", parser.currentToken());
                parser.getText();
        }

        parser.nextToken();
    }
}
