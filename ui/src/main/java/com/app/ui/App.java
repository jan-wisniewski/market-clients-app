package com.app.ui;

import com.app.service.MarketService;
import com.app.ui.menu.MenuService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        try {
            final String CLIENTS = "./resources/data/clients.json";
            final String PRODUCTS = "./resources/data/products.json";
            final String PREFERENCES = "./resources/data/preferences.txt";
            MarketService marketService = new MarketService(CLIENTS, PRODUCTS, PREFERENCES);
            MenuService menuService = new MenuService(marketService);
            menuService.mainMenu();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
