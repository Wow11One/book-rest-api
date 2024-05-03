package com.profitsoft.restapi.controller;

import com.profitsoft.restapi.dto.book.FilterDto;
import com.profitsoft.restapi.dto.book.ListBookDto;
import com.profitsoft.restapi.dto.book.QueryBookDto;
import com.profitsoft.restapi.dto.book.RequestBookDto;
import com.profitsoft.restapi.dto.book.ResponseBookDto;
import com.profitsoft.restapi.dto.book.UploadBookDto;
import com.profitsoft.restapi.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/books")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BookController {

    BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseBookDto create(@Valid @RequestBody RequestBookDto requestBookDto) {
        return bookService.create(requestBookDto);
    }

    @GetMapping("/{id}")
    public ResponseBookDto findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping("/_list")
    public ListBookDto getList(@Valid @RequestBody QueryBookDto queryBookDto) {
        return bookService.getList(queryBookDto);
    }

    @PostMapping(value = "/_report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getReport(@RequestBody(required = false) FilterDto filterDto,
                          HttpServletResponse response) {
        bookService.getReport(filterDto, response);
    }

    @PostMapping("/upload")
    public UploadBookDto uploadBooksFromJson(@RequestParam("file") MultipartFile multipartFile) {
        return bookService.uploadBooksFromJson(multipartFile);
    }

    @PutMapping("/{id}")
    public ResponseBookDto updateById(@Valid @RequestBody RequestBookDto requestBookDto,
                                      @PathVariable Long id) {
        return bookService.updateById(id, requestBookDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
