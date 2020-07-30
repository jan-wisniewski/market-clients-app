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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MarketService {
    private final List<Client> CLIENTS;
    private final List<Product> PRODUCTS;
    private Map<Client, List<Product>> purchases;
    private final Map<Long, String> PREFERENCES;

    public MarketService(String clientsFile, String productsFile, String preferencesFile) {
        CLIENTS = new ClientsJsonConverter(clientsFile).fromJson().orElseThrow();
        PRODUCTS = new ProductsJsonConverter(productsFile).fromJson().orElseThrow();
        PREFERENCES = readCategoriesFromString(readFile(preferencesFile));
        this.purchases = init();
    }

    public Map<Client, List<Product>> getPurchases() {
        return purchases;
    }

    public Map<Client, List<Product>> init() {
        Map<Client, List<Product>> map = new HashMap<>();
        CLIENTS.forEach(c -> map.put(c, buyProductsForClient(c, userPreferences(c))));
        return map;
    }

    public String showAllProducts() {
        return PRODUCTS.stream()
                .map(p -> p.getName() + " (" + p.getCategory() + ") - " + p.getPrice())
                .collect(Collectors.joining("\n"));
    }

    public String showAllClients() {
        return CLIENTS.stream()
                .map(c -> c.getName() + " " + c.getSurname() + " (" + c.getAge() + "), CASH: " + c.getCash())
                .collect(Collectors.joining("\n"));
    }

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
                        (s1, s2) -> s1,
                        LinkedHashMap::new
                ));
    }

    public Product showMostOftenBoughtProduct() {
        return showProductsStatistics().entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();
    }

    public Product showLeastOftenBoughtProduct() {
        return showProductsStatistics().entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();
    }

    public Map<Product, Long> showProductsStatistics() {
        return purchases.entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.groupingBy(Function.identity(),
                        Collectors.counting()));
    }

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

    private List<Product> buyProductsForClient(Client client, List<Long> categoriesID) {
        if (categoriesID == null) {
            throw new MarketServiceException("CategoryID is null");
        }
        BigDecimal clientCash = client.getCash();
        List<Product> totalProducts = new ArrayList<>();
        List<Product> productsFromCategory;
        for (Long categoryID : categoriesID) {
            productsFromCategory = buyProductsFromCategory(categoryID, clientCash);
            if (productsFromCategory.size() != 0) {
                totalProducts.addAll(productsFromCategory);
                clientCash = clientCash.subtract(calculateValueOfProducts(productsFromCategory));
            }
        }
        return totalProducts;
    }

    private List<Product> buyProductsFromCategory(Long categoryID, BigDecimal cash) {
        AtomicReference<BigDecimal> sum = new AtomicReference<>(BigDecimal.ZERO);
        List<Product> boughtProducts = new ArrayList<>();
        PRODUCTS
                .stream()
                .filter(p -> p.getCategory().name().equals(findPreferencesById(categoryID)))
                .sorted(Comparator.comparing(this::calculateProductRatio))
                .collect(Collectors.toList())
                .forEach(product -> {
                    if (sum.get().add(product.getPrice()).compareTo(cash) <= 0) {
                        boughtProducts.add(product);
                        sum.set(sum.get().add(product.getPrice()));
                    }
                });
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
        return PREFERENCES.get(id);
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