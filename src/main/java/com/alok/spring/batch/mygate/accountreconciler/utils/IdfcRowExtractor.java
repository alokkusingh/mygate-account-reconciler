package com.alok.spring.batch.mygate.accountreconciler.utils;

import com.alok.spring.batch.mygate.accountreconciler.model.BankAccountTransaction;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

@Slf4j
public class IdfcRowExtractor implements RowExtractor<BankAccountTransaction> {
    private String rowStartRegex = "[0-9]{2}-[a-zA-Z]{3}-[0-9]{4}";

    @SneakyThrows
    @Override
    public BankAccountTransaction extract(Row row) {

        if (row.getPhysicalNumberOfCells() == 7
                && Pattern.matches(rowStartRegex,row.getCell(0).toString())) {

            return BankAccountTransaction.builder()
                    .bankDate(new SimpleDateFormat("dd-MMM-yyyy").parse(row.getCell(0).getStringCellValue()))
                    .utrNo(row.getCell(2).getStringCellValue().split("/")[1])
                    .build();
        }

        return null;
    }
}
