package com.app.service;

import com.app.persistence.converters.impl.ClientsJsonConverter;
import com.app.persistence.converters.impl.ProductsJsonConverter;
import com.app.persistence.models.Client;
import com.app.persistence.models.Product;
import com.app.service.enums.FileType;
import com.app.service.exceptions.MarketServiceException;

import java.io.File;
import java.lang.ref.Cleaner;
import java.util.List;
import java.util.Map;

public class MarketService {
    private final List<Client> CLIENTS;
    private final List<Product> PRODUCTS;
    private Map<Client, List<Product>> purchases;

    public MarketService(String clientsFile, String productsFile) {
        CLIENTS = new ClientsJsonConverter(clientsFile).fromJson().orElseThrow();
        PRODUCTS = new ProductsJsonConverter(productsFile).fromJson().orElseThrow();
        this.purchases = init();
    }

    public Map<Client, List<Product>> init() {
        return Map.of();
    }

}
