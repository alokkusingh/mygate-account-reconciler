package com.alok.spring.batch.mygate.accountreconciler.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class ExcelxReader<T> implements ResourceAwareItemReaderItemStream {
    private Resource resource;
    private List<T> items;
    private int currentIndex = 0;
    private FileInputStream sheetInputFile;
    private RowExtractor<T> rowExtractor;

    public void setRowExtractor(RowExtractor<T> rowExtractor) {
        this.rowExtractor = rowExtractor;
    }


    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public T read() {
        if (currentIndex < items.size()) {
            return items.get(currentIndex++);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.debug("Started Processing File: {}",resource.getFile().getPath());
        if (executionContext.containsKey("current.index")) {
            currentIndex = executionContext.getInt("current.index");
        } else {
            currentIndex = 0;
            // Creating Input Stream
            sheetInputFile = new FileInputStream(new File(resource.getFile().getPath()));
            readLines();
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {
        if (sheetInputFile != null) {
            try {
                sheetInputFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readLines() throws IOException {
        items = new LinkedList<>();

        // Create a workbook using the File System
        XSSFWorkbook myWorkBook = new XSSFWorkbook(sheetInputFile);

        // Get the first sheet from workbook
        XSSFSheet mySheet = myWorkBook.getSheet("Account Statement");

        //Row iterator
        Iterator rowIter = mySheet.rowIterator();

        while (rowIter.hasNext()) {
            T obj = rowExtractor.extract((XSSFRow) rowIter.next());
            if (obj != null) {
               items.add(obj);
            }
        }
    }

}
