package com.app.persistence.converters.impl.collections;

import com.app.persistence.converters.generic.Converter;
import com.app.persistence.converters.generic.ReadTxtFile;
import com.app.persistence.models.Client;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ClientsTxtConverter implements Converter<Client> {

    private String fileContent;

    public ClientsTxtConverter(String filePath) {
        this.fileContent = new ReadTxtFile(filePath).getFileContent();
    }

    @Override
    public List<Client> convert() {
        return fileContent.lines()
                .map(l -> Client.builder()
                        .name(l.split(";")[0])
                        .surname(l.split(";")[1])
                        .age(Integer.parseInt(l.split(";")[2]))
                        .cash(new BigDecimal(l.split(";")[3]))
                        .preferences(Long.parseLong(l.split(";")[4]))
                        .build()
                )
                .collect(Collectors.toList());
    }
}
