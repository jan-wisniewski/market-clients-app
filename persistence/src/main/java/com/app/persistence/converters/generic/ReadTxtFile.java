package com.app.persistence.converters.generic;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ReadTxtFile {

    private String fileContent;

    public ReadTxtFile(String filePath) {
        this.fileContent = fileReader(filePath);
    }

    public String getFileContent() {
        return fileContent;
    }

    private String fileReader(String filePath) {
        StringBuilder file = new StringBuilder();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(l -> file.append(l).append("\n"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return file.toString();
    }
}
