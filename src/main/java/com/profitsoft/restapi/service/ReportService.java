package com.profitsoft.restapi.service;

import com.profitsoft.restapi.entity.Book;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

public interface ReportService {

    HSSFWorkbook generateExcel(List<Book> books);
}
