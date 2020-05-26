package com.alok.spring.batch.mygate.accountreconciler.utils;

import org.apache.poi.hssf.usermodel.HSSFRow;

public interface RowExtractor<T> {
    T extract(HSSFRow row);
}
