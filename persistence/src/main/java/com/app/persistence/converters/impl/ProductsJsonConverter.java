package com.app.persistence.converters.impl;

import com.app.persistence.converters.generic.JsonConverter;
import com.app.persistence.models.Product;

import java.util.List;

public class ProductsJsonConverter extends JsonConverter<List<Product>> {
    public ProductsJsonConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
