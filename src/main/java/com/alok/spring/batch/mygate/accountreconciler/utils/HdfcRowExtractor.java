package com.alok.spring.batch.mygate.accountreconciler.utils;

import com.alok.spring.batch.mygate.accountreconciler.model.HdfcBankTransaction;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

@Slf4j
public class HdfcRowExtractor implements RowExtractor<HdfcBankTransaction> {
    private String rowStartRegex = "[0-9]{2}/[0-9]{2}/[0-9]{2}";

    @SneakyThrows
    @Override
    public HdfcBankTransaction extract(HSSFRow row) {
        HdfcBankTransaction hdfcBankTransaction = null;

        if (row.getPhysicalNumberOfCells() == 7
                && Pattern.matches(rowStartRegex,row.getCell(0).toString())) {
            hdfcBankTransaction = new HdfcBankTransaction();
            hdfcBankTransaction.setBankDate(new SimpleDateFormat("dd/MM/yy").parse(row.getCell(0).getStringCellValue()));
            hdfcBankTransaction.setUtrNo(row.getCell(2).getStringCellValue());
        }

        return hdfcBankTransaction;
    }
}
