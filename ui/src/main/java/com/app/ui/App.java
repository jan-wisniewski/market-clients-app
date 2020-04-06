package com.app.ui;

import com.app.persistence.converters.impl.collections.ClientsTxtConverter;
import com.app.persistence.converters.impl.collections.ProductsTxtConverter;

public class App {
    public static void main(String[] args) {
        try {
            final String CLIENTS = "./resources/data/clients.txt";
            final String PRODUCTS = "./resources/data/products.txt";
            var clients = new ClientsTxtConverter(CLIENTS).convert();
            var products = new ProductsTxtConverter(PRODUCTS).convert();
            System.out.println(clients);
            System.out.println("---------");
            System.out.println(products);
//            MarketService marketService = new MarketService(CLIENTS,PRODUCTS);
//            MenuService menuService = new MenuService(marketService);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
