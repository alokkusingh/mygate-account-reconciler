package com.alok.spring.batch.mygate.accountreconciler.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SkipRecordOnErrorPolicy implements SkipPolicy {
    @Override
    public boolean shouldSkip(Throwable error, int skipCount) throws SkipLimitExceededException {
        log.error(error.getMessage());
        log.debug("Error: ", error);
        log.warn("Skipped Record, count {}", skipCount + 1);
        return true;
    }
}
