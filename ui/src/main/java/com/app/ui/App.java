package com.app.ui;

import com.app.service.MarketService;

public class App {
    public static void main(String[] args) {
        try {
            final String CLIENTS = "./resources/data/clients.json";
            final String PRODUCTS = "./resources/data/products.json";
            MarketService marketService = new MarketService(CLIENTS, PRODUCTS);
            System.out.println(marketService.showPurchases());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
