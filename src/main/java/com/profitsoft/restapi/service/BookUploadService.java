package com.profitsoft.restapi.service;

import com.profitsoft.restapi.entity.Book;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface BookUploadService {

    List<Book> analyse(InputStream inputStream);
    Integer getSuccessfulCount();
    Integer getFailedCount();
}
