package com.profitsoft.restapi.service;

import com.profitsoft.restapi.dto.book.FilterDto;
import com.profitsoft.restapi.dto.book.ListBookDto;
import com.profitsoft.restapi.dto.book.QueryBookDto;
import com.profitsoft.restapi.dto.book.ResponseBookDto;
import com.profitsoft.restapi.dto.book.RequestBookDto;
import com.profitsoft.restapi.dto.book.UploadBookDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {

    ResponseBookDto create(RequestBookDto requestBookDto);

    ResponseBookDto findById(Long id);

    ListBookDto getList(QueryBookDto queryBookDto);

    void getReport(FilterDto filterDto, HttpServletResponse response);

    UploadBookDto uploadBooksFromJson(MultipartFile multipartFile);

    ResponseBookDto updateById(Long id, RequestBookDto requestBookDto);

    void deleteById(Long id);
}
