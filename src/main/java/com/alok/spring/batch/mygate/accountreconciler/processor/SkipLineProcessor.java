package com.alok.spring.batch.mygate.accountreconciler.processor;

import com.alok.spring.batch.mygate.accountreconciler.model.Header;
import com.alok.spring.batch.mygate.accountreconciler.repository.HeaderRepository;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SkipLineProcessor implements LineCallbackHandler {
    @Autowired
    private HeaderRepository headerRepository;

    @Override
    public void handleLine(String s) {

        headerRepository.save(
                Header.builder()
                .line(s)
                .build()
        );
    }
}
