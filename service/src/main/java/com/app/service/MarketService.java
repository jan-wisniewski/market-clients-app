package com.app.service;

import com.app.persistence.converters.impl.ClientsJsonConverter;
import com.app.persistence.converters.impl.ProductsJsonConverter;
import com.app.persistence.enums.Category;
import com.app.persistence.models.Client;
import com.app.persistence.models.Product;
import com.app.service.exceptions.MarketServiceException;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MarketService {
    private final List<Client> CLIENTS;
    private final List<Product> PRODUCTS;
    private final Map<Client, List<Product>> purchases;

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
        CLIENTS.forEach(c -> map.put(c, buyProductsForClient(c)));
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

    private BigDecimal calculateValueOfProducts(List<Product> products) {
        return products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal::add)
                .orElseThrow();
    }

    private double calculateProductRatio(Product p) {
        return p.getPrice().doubleValue() / p.getQuantity();
    }


    private List<Product> buyProductsForClient(Client client) {
        List<Long> categoriesID = userPreferences(client);
        List<Product> productsForUser = getProductsWithCategory(categoriesID);
        List<Product> boughtProds = new ArrayList<>();
        AtomicReference<BigDecimal> currentClientCash = new AtomicReference<>(client.getCash());

        productsForUser.sort(
                Comparator.comparing(
                        (Function<Product, Integer>) p -> p.getCategory().ordinal())
                        .thenComparing(this::calculateProductRatio));

        productsForUser
                .forEach(p -> {
                    if (p.getPrice().compareTo(currentClientCash.get()) < 0) {
                        boughtProds.add(p);
                        currentClientCash.set(currentClientCash.get().subtract(p.getPrice()));
                    }
                });
        return boughtProds;

    }

    private List<Product> getProductsWithCategory(List<Long> categoriesID) {
        return PRODUCTS
                .stream()
                .filter(p -> categoriesID.contains((long) p.getCategory().ordinal() + 1))
                .collect(Collectors.toList());
    }

    private List<Long> userPreferences(Client c) {
        if (c == null) {
            throw new MarketServiceException("Client is null");
        }
        String[] ids = String.valueOf(c.getPreferences()).split("");
        return Arrays.stream(ids)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

}