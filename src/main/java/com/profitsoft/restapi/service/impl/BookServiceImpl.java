package com.profitsoft.restapi.service.impl;

import com.profitsoft.restapi.dto.book.FilterDto;
import com.profitsoft.restapi.dto.book.ListBookDto;
import com.profitsoft.restapi.dto.book.QueryBookDto;
import com.profitsoft.restapi.dto.book.ResponseBookDto;
import com.profitsoft.restapi.dto.book.RequestBookDto;
import com.profitsoft.restapi.dto.book.SimpleBookDto;
import com.profitsoft.restapi.dto.book.UploadBookDto;
import com.profitsoft.restapi.entity.Book;
import com.profitsoft.restapi.entity.Book_;
import com.profitsoft.restapi.exception.ApiException;
import com.profitsoft.restapi.mapper.BookMapper;
import com.profitsoft.restapi.repository.BookRepository;
import com.profitsoft.restapi.service.BookService;
import com.profitsoft.restapi.service.BookUploadService;
import com.profitsoft.restapi.service.ReportService;
import com.profitsoft.restapi.service.specification.BookSpecs;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookServiceImpl implements BookService {

    BookRepository bookRepository;
    BookMapper bookMapper;
    ReportService reportService;
    Sort sort = Sort.by(Sort.Direction.ASC, Book_.ID);

    @Override
    @Transactional
    public ResponseBookDto create(RequestBookDto requestBookDto) {
        Book book = bookMapper.toEntity(requestBookDto);

        bookRepository.save(book);
        return bookMapper.toResponseDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseBookDto findById(Long id) {
        Book book = findEntityById(id);

        return bookMapper.toResponseDto(book);
    }

    @Transactional(readOnly = true)
    public ListBookDto getList(QueryBookDto queryBookDto) {
        Specification<Book> specification = createFilter(
                queryBookDto.getFilterDto().getAuthorId(),
                queryBookDto.getFilterDto().getGenre(),
                queryBookDto.getFilterDto().getPublicationHouse()
        );
        Pageable pageable = PageRequest.of(
                queryBookDto.getPage() - 1,
                queryBookDto.getSize(),
                sort
        );
        Page<Book> resultPage = bookRepository.findAll(specification, pageable);
        List<SimpleBookDto> resultList = resultPage.getContent()
                .stream()
                .map(bookMapper::toSimpleDto)
                .toList();

        return ListBookDto.builder()
                .list(resultList)
                .totalPages(resultPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public void getReport(FilterDto filterDto, HttpServletResponse response) {
        Specification<Book> specification = filterDto == null
                ? Specification.allOf()
                : createFilter(filterDto.getAuthorId(), filterDto.getGenre(), filterDto.getPublicationHouse());
        List<Book> books = bookRepository.findAll(specification, sort);
        HSSFWorkbook workbook = reportService.generateExcel(books);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=book-report.xls");
            workbook.write(outputStream);
        } catch (Exception exception) {
            String message = exception.getMessage();
            log.error(message);
            throw new ApiException(message);
        }
    }

    public UploadBookDto uploadBooksFromJson(MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // I used a custom class to read json file, because it was required by our task
            BookUploadService bookUploadService = getBookUploadService();
            List<Book> booksToBeSaved = bookUploadService.analyse(inputStream);
            bookRepository.saveAll(booksToBeSaved);

            log.info("{} books saved successfully", booksToBeSaved.size());

            return UploadBookDto.builder()
                    .successful(bookUploadService.getSuccessfulCount())
                    .failed(bookUploadService.getFailedCount())
                    .build();
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseBookDto updateById(Long id, RequestBookDto requestBookDto) {
        Book bookToBeUpdated = findEntityById(id);

        bookMapper.mergeToBookEntity(requestBookDto, bookToBeUpdated);
        bookRepository.save(bookToBeUpdated);
        return bookMapper.toResponseDto(bookToBeUpdated);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    private Book findEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("book with such id does not exist"));
    }

    private Specification<Book> createFilter(Long authorId, String genre, String publicationHouse) {
        List<Specification<Book>> specifications = new ArrayList<>();

        Optional.ofNullable(authorId)
                .ifPresent(id -> specifications.add(BookSpecs.equalsAuthorId(id)));
        Optional.ofNullable(genre)
                .ifPresent(data -> specifications.add(BookSpecs.equalsGenre(data)));
        Optional.ofNullable(publicationHouse)
                .ifPresent(data -> specifications.add(BookSpecs.equalsPublicationHouse(data)));

        return BookSpecs.buildQuery(specifications);
    }

    @Lookup
    public BookUploadService getBookUploadService() {
        return null;
    }
}
