package com.alok.spring.batch.mygate.accountreconciler.utils;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@Slf4j
public class FileScanner {

    private String dirPath;
    
    @Builder.Default
    private String fileRegex = ".*";

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public void setFileRegex(String fileRegex) {
        this.fileRegex = fileRegex;
    }

    public List<String> getFiles() {
        Assert.notNull(dirPath, "Directory Path must be set!");
        File dir = new File(dirPath);
        log.info("Scanning dir {} for file {}", dirPath, fileRegex);

        try (Stream<Path> walk = Files.walk(Paths.get(dirPath), 1)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(file -> Pattern.matches(fileRegex,file.getFileName().toString()))
                    .map(file -> file.toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
