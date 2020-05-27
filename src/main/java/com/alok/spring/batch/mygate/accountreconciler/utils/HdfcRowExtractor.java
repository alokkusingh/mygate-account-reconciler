package com.alok.spring.batch.mygate.accountreconciler.utils;

import com.alok.spring.batch.mygate.accountreconciler.model.HdfcBankTransaction;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

@Slf4j
public class HdfcRowExtractor implements RowExtractor<HdfcBankTransaction> {
    private String rowStartRegex = "[0-9]{2}/[0-9]{2}/[0-9]{2}";

    @SneakyThrows
    @Override
    public HdfcBankTransaction extract(HSSFRow row) {

        if (row.getPhysicalNumberOfCells() == 7
                && Pattern.matches(rowStartRegex,row.getCell(0).toString())) {

            return HdfcBankTransaction.builder()
                    .bankDate(new SimpleDateFormat("dd/MM/yy").parse(row.getCell(0).getStringCellValue()))
                    .utrNo(row.getCell(2).getStringCellValue())
                    .build();
        }

        return null;
    }
}
