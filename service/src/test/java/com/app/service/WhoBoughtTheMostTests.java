package com.app.service;

import com.app.persistence.enums.Category;
import com.app.persistence.models.Client;
import com.app.persistence.models.Product;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class WhoBoughtTheMostTests {

    private static final String PRODUCTS_FILENAME = "test-products.json";
    private static final String CLIENTS_FILENAME = "test-clients.json";
    private static MarketService marketService;

    private Product timer, czapka, lopatka;
    private Client jan, magdalena;
    private Map<Client, List<Product>> purchases;

    @BeforeAll
    public static void beforeAll() {
        final String[] elements = MarketService.class
                .getClassLoader()
                .getResource("")
                .toString()
                .split("target");
        final String PATH = elements[0] + "src/test/java/resources/";
        final String[] FULL_PATH_PRODUCTS = (PATH + PRODUCTS_FILENAME).split("file:/");
        final String[] FULL_PATH_CLIENTS = (PATH + CLIENTS_FILENAME).split("file:/");
        marketService = new MarketService(FULL_PATH_CLIENTS[1], FULL_PATH_PRODUCTS[1]);
    }

    @BeforeEach
    public void setUp() {
        timer = Product.builder()
                .name("TIMER")
                .category(Category.GOSPODARSTWO_DOMOWE)
                .price(BigDecimal.valueOf(10))
                .quantity(4)
                .build();
        czapka = Product.builder()
                .name("CZAPKA")
                .quantity(3)
                .price(BigDecimal.valueOf(10))
                .category(Category.ODZIEZ)
                .build();
        lopatka = Product.builder()
                .name("LOPATKA SILIKONOWA")
                .category(Category.GOSPODARSTWO_DOMOWE)
                .price(BigDecimal.valueOf(10))
                .quantity(5)
                .build();
        jan = Client.builder()
                .name("JAN")
                .surname("KOWAL")
                .age(20)
                .cash(BigDecimal.valueOf(1200))
                .preferences(34L)
                .build();
        magdalena = Client.builder()
                .name("MAGDALENA")
                .surname("MALINOWSKA")
                .preferences(4L)
                .cash(BigDecimal.valueOf(1000))
                .age(31)
                .build();
        purchases = Map.of(
                jan, List.of(timer, czapka, lopatka),
                magdalena, List.of(czapka)
        );
    }

    @Test
    @DisplayName("if returning client who bought the most is correct")
    public void test1(){
        Assertions.assertEquals(jan,marketService.whoBoughtTheMost());
    }

}
