package com.app.service;

import com.app.persistence.converters.impl.ClientsJsonConverter;
import com.app.persistence.converters.impl.ProductsJsonConverter;
import com.app.persistence.enums.Category;
import com.app.persistence.models.Client;
import com.app.persistence.models.Product;
import com.app.service.exceptions.MarketServiceException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MarketService {
    private final List<Client> CLIENTS;
    private final List<Product> PRODUCTS;
    private Map<Client, List<Product>> purchases;
    private final String PREFERENCES_PATH = "./resources/data/preferences.txt";

    public MarketService(String clientsFile, String productsFile) {
        CLIENTS = new ClientsJsonConverter(clientsFile).fromJson().orElseThrow();
        PRODUCTS = new ProductsJsonConverter(productsFile).fromJson().orElseThrow();
        this.purchases = init();
    }

    public Map<Client, List<Product>> getPurchases() {
        return purchases;
    }

    public Map<Client, List<Product>> init() {
        Map<Client, List<Product>> map = new HashMap<>();
        CLIENTS.forEach(c -> map.put(c, prepareUserBoughtProducts(c, userPreferences(c))));
        return map;
    }

    public String showAllProducts() {
        return PRODUCTS.stream()
                .map(p -> p.getName()+" ("+p.getCategory()+") - "+p.getPrice())
                .collect(Collectors.joining("\n"));
    }

    public String showAllClients(){
        return CLIENTS.stream()
                .map(c -> c.getName()+" "+c.getSurname()+" ("+c.getAge()+"), CASH: " +c.getCash())
                .collect(Collectors.joining("\n"));
    }

    //[4] Przygotuj zestawienie kategorii, w którym umieścisz nazwy kategorii
    //posortowane malejąco według popularności ich wybierania.
    public Map<Category, Long> showCategoriesStatistics() {
        return purchases.entrySet().stream()
                .flatMap(p -> p.getValue().stream())
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (s1,s2) -> s1,
                        LinkedHashMap::new
                ));
    }

    //[3] podaj dane produktu najczęściej kupowany
    public Product showMostOftenBoughtProduct() {
        return showProductsStatistics().entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();
    }

    //[3] podaj dane produktu najrzadziej kupowany
    public Product showLeastOftenBoughtProduct() {
        return showProductsStatistics().entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();
    }

    //[3] Przygotuj zestawienie produktów, które posiada informacje na temat
    //produktu oraz ile razy był wybierany przez wszystkich klientów.
    public Map<Product, Long> showProductsStatistics() {
        return purchases.entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.groupingBy(Function.identity(),
                        Collectors.counting()));
    }

    //[2] Wyświetl dane klienta, który zakupił produkty o łącznie najwyższej wartości.
    public Client whoSpentTheMost() {
        return purchases.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> calculateValueOfProducts(e.getValue()),
                        (s1, s2) -> s1
                )).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();
    }

    //[1] Wyświetl dane klienta, który zakupił najwięcej produktów.
    public Client whoBoughtTheMost() {
        return purchases.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().size()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();
    }

    private String showClientInfo(Client c) {
        if (c == null) {
            throw new MarketServiceException("Client is null");
        }
        return c.getName() + " "
                + c.getSurname() +
                ", cash: " + c.getCash() +
                ", preferences: " + showClientPreferences(userPreferences(c));
    }

    private String showClientPreferences(List<Long> categoryID) {
        if (categoryID == null) {
            throw new MarketServiceException("Category ID is null");
        }
        return categoryID.stream()
                .map(this::findPreferencesById)
                .collect(Collectors.joining(", "));
    }

    private BigDecimal calculateValueOfProducts(List<Product> products) {
        return products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal::add)
                .orElseThrow();
    }

    private double calculateProductRatio(Product p) {
        return p.getPrice().doubleValue() / p.getQuantity();
    }

    private List<Product> prepareUserBoughtProducts(Client client, List<Long> categoryID) {
        if (categoryID == null) {
            throw new MarketServiceException("CategoryID is null");
        }
        BigDecimal clientCash = client.getCash();
        List<Product> totalProducts = new ArrayList<>();
        List<Product> productsFromCategory;
        for (Long aLong : categoryID) {
            productsFromCategory = buyProductsFromCategory(aLong, clientCash);
            if (productsFromCategory.size() != 0) {
                totalProducts.addAll(productsFromCategory);
                clientCash = clientCash.subtract(calculateValueOfProducts(productsFromCategory));
            }
        }
        return totalProducts;
    }

    public List<Product> buyProductsFromCategory(Long id, BigDecimal cash) {
        List<Product> productsFromCategory = PRODUCTS.stream()
                .filter(p -> p.getCategory().name().equals(findPreferencesById(id)))
                .sorted(Comparator.comparing(this::calculateProductRatio))
                .collect(Collectors.toList());
        BigDecimal clientCash = cash;
        List<Product> boughtProducts = new ArrayList<>();
        for (Product p : productsFromCategory) {
            if (p.getPrice().compareTo(clientCash) <= 0) {
                boughtProducts.add(p);
                clientCash = clientCash.subtract(p.getPrice());
            }
        }
        return boughtProducts;
    }

    private List<Long> userPreferences(Client c) {
        if (c == null) {
            throw new MarketServiceException("Client is null");
        }
        List<Long> preferencesList = new ArrayList<>();
        long currentPreferences = c.getPreferences();
        while (currentPreferences != 0) {
            preferencesList.add(currentPreferences % 10);
            currentPreferences = currentPreferences / 10;
        }
        return preferencesList;
    }

    private String findPreferencesById(long id) {
        return readCategoriesFromString(readFile(PREFERENCES_PATH))
                .get(id);
    }

    private Map<Long, String> readCategoriesFromString(String content) {
        if (content == null) {
            throw new MarketServiceException("Content of file is null");
        }
        return Arrays.stream(content.split("\n"))
                .collect(Collectors.toMap(
                        e -> Long.parseLong(e.split(" ")[0]),
                        e -> e.split(" ")[1],
                        (s1, s2) -> s1
                ));
    }

    private String readFile(String filename) {
        if (filename == null) {
            throw new MarketServiceException("Filename is null");
        }
        StringBuilder sb = new StringBuilder();
        try (Stream<String> lines = Files.lines(Paths.get(filename))) {
            lines.forEach(l -> sb.append(l).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}