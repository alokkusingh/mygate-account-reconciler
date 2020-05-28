package com.alok.spring.batch.mygate.accountreconciler.processor;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.File;

@Slf4j
@Builder
public class FileArchiveTasklet implements Tasklet, InitializingBean {

    private Resource resource;

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

       File file = resource.getFile();
       file.renameTo(new File(file.getAbsoluteFile().getAbsolutePath() + "." + System.currentTimeMillis() + ".processed"));
       log.info("File renamed to {}.{}.{}", file, System.currentTimeMillis(), "processed");

        return RepeatStatus.FINISHED;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(resource, "resource must be set");
    }
}
