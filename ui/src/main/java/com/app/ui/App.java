package com.app.ui;

import com.app.service.MarketService;
import com.app.ui.menu.MenuService;

public class App {
    public static void main(String[] args) {
        final String CLIENTS = "./resources/data/clients.json";
        final String PRODUCTS = "./resources/data/products.json";
        MarketService marketService = new MarketService(CLIENTS, PRODUCTS);
        MenuService menuService = new MenuService(marketService);
        menuService.mainMenu();
    }
}
