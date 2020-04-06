package com.app.ui;

import com.app.persistence.converters.impl.ClientsJsonConverter;
import com.app.persistence.converters.impl.ProductsJsonConverter;
import com.app.service.MarketService;

public class App {
    public static void main(String[] args) {
        try {
            final String CLIENTS = "./resources/data/clients.json";
            final String PRODUCTS = "./resources/data/products.json";
            MarketService marketService = new MarketService(CLIENTS, PRODUCTS);
//            MenuService menuService = new MenuService(marketService);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
