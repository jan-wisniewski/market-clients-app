package com.app.ui.menu;

import com.app.service.MarketService;
import com.app.ui.user_data.UserDataService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MenuService {

    private final MarketService marketService;

    public void mainMenu() {
        while (true) {
            try {
                System.out.println("0. Exit");
                System.out.println("1. All clients");
                System.out.println("2. All products");
                System.out.println("3. All purchases");
                System.out.println("4. Customer who bought the most");
                System.out.println("5. Customer who spent the most");
                System.out.println("6. Products statistics");
                System.out.println("7. Product that was least frequently bought");
                System.out.println("8. Product that was most often bought");
                System.out.println("9. Product categories statistics");
                int option = UserDataService.getInteger("Choose option");
                switch (option){
                    case 0 -> {
                        System.out.println("Goodbye");
                        return;
                    }
                    case 1 -> option1();
                    case 2 -> option2();
                    case 3 -> option3();
                    case 4 -> option4();
                    case 5 -> option5();
                    case 6 -> option6();
                    case 7 -> option7();
                    case 8 -> option8();
                    case 9 -> option9();
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    private void option9() {
        System.out.println(marketService.showCategoriesStatistics());
    }

    private void option8() {
        System.out.println(marketService.showMostOftenBoughtProduct());
    }

    private void option7() {
        System.out.println(marketService.showLeastOftenBoughtProduct());
    }

    private void option6() {
        System.out.println(marketService.showProductsStatistics());
    }

    private void option5() {
        System.out.println(marketService.whoSpentTheMost());
    }

    private void option4() {
        System.out.println(marketService.whoBoughtTheMost());
    }

    private void option3() {
        System.out.println(marketService.getPurchases());
    }

    private void option2() {
        System.out.println(marketService.showAllProducts());
    }

    private void option1() {
        System.out.println(marketService.showAllClients());
    }

}
