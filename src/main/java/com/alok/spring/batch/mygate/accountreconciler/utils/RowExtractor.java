package com.alok.spring.batch.mygate.accountreconciler.utils;

import org.apache.poi.ss.usermodel.Row;

public interface RowExtractor<T> {
    T extract(Row row);
}
