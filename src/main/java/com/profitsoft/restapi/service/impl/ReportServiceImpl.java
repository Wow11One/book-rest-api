package com.profitsoft.restapi.service.impl;

import com.profitsoft.restapi.entity.Book;
import com.profitsoft.restapi.exception.ApiException;
import com.profitsoft.restapi.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.BorderStyle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Override
    public HSSFWorkbook generateExcel(List<Book> books) {
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            HSSFSheet sheet = workbook.createSheet("Book info");
            HSSFRow row = sheet.createRow(0);

            createHeader(row, workbook);

            int i = 1;
            HSSFRow bookRow;
            for (Book book : books) {
                bookRow = sheet.createRow(i);
                createBookRow(bookRow, book, workbook);
                i++;
            }

            autoSizeColumns(sheet);

            return workbook;
        } catch (Exception exception) {
            String message = exception.getMessage();
            log.error(message);
            throw new ApiException(message);
        }
    }

    private void createHeader(HSSFRow row, HSSFWorkbook workbook) {
        List<String> headers = List.of(
                "Id",
                "Title",
                "Publication year",
                "House publication",
                "Genre",
                "Circulation",
                "Page amount",
                "Author's id",
                "Author's name"
        );

        HSSFFont font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        setBorders(style);

        int i = 0;
        Cell cell;
        for (String header : headers) {
            cell = row.createCell(i++);
            cell.setCellStyle(style);
            cell.setCellValue(header);
        }
    }

    private void createBookRow(HSSFRow bookRow, Book book, HSSFWorkbook workbook) {
        List<Cell> cells = new ArrayList<>();
        CellStyle style = workbook.createCellStyle();
        setBorders(style);

        Cell cell;
        for (int i = 0; i < 9; i++) {
            cell = bookRow.createCell(i);
            cell.setCellStyle(style);
            cells.add(cell);
        }

        cells.get(0).setCellValue(book.getId());
        cells.get(1).setCellValue(book.getTitle());
        cells.get(2).setCellValue(book.getYearPublished());
        cells.get(3).setCellValue(book.getPublicationHouse());
        cells.get(4).setCellValue(book.getGenre().getName());
        cells.get(5).setCellValue(book.getCirculation());
        cells.get(6).setCellValue(book.getPageAmount());
        cells.get(7).setCellValue(book.getAuthor().getId());
        cells.get(8).setCellValue(book.getAuthor().getName());
    }

    private void setBorders(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
    }

    private void autoSizeColumns(HSSFSheet sheet) {
        if (sheet.getPhysicalNumberOfRows() > 0) {
            HSSFRow row = sheet.getRow(sheet.getFirstRowNum());
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                sheet.autoSizeColumn(columnIndex);
                int currentColumnWidth = sheet.getColumnWidth(columnIndex);
                sheet.setColumnWidth(columnIndex, (currentColumnWidth + 2500));
            }
        }
    }
}
