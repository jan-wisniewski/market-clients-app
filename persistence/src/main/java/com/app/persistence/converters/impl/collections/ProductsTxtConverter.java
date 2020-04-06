package com.app.persistence.converters.impl.collections;

import com.app.persistence.converters.generic.Converter;
import com.app.persistence.converters.generic.ReadTxtFile;
import com.app.persistence.enums.Category;
import com.app.persistence.models.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProductsTxtConverter implements Converter<Product> {

    private String fileContent;

    public ProductsTxtConverter(String filePath) {
        this.fileContent = new ReadTxtFile(filePath).getFileContent();
    }

    @Override
    public List<Product> convert() {
        return fileContent.lines()
                .map(l -> Product.builder()
                        .name(l.split(";")[0])
                        .quantity(Integer.parseInt(l.split(";")[1]))
                        .price(new BigDecimal(l.split(";")[2]))
                        .category(Category.values()[Integer.parseInt(l.split(";")[3])-1])
                        .build()
                )
                .collect(Collectors.toList());
    }
}
