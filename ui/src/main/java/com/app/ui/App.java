package com.app.ui;

import com.app.service.MarketService;
import com.app.ui.menu.MenuService;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) {
        try {
            final String CLIENTS = "./resources/data/clients.json";
            final String PRODUCTS = "./resources/data/products.json";
            MarketService marketService = new MarketService(CLIENTS, PRODUCTS);
            MenuService menuService = new MenuService(marketService);
            menuService.mainMenu();

//            System.out.println(marketService.showPurchases());
//            System.out.println("----[who bought the most]----");
//            System.out.println(marketService.whoBoughtTheMost());
//            System.out.println("----[who spent the most]----");
//            System.out.println(marketService.whoSpentTheMost());
//            System.out.println("----[products statistics]----");
//            System.out.println(marketService.showProductsStatistics());
//            System.out.println("----[most often bought]----");
//            System.out.println(marketService.showMostOftenBoughtProduct());
//            System.out.println("----[show least often bought]----");
//            System.out.println(marketService.showLeastOftenBoughtProduct());
//            System.out.println("----[most popular categories]----");
//            System.out.println(marketService.showCategoriesStatistics());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
