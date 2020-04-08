package com.app.service;

import com.app.persistence.converters.impl.ClientsJsonConverter;
import com.app.persistence.converters.impl.ProductsJsonConverter;
import com.app.persistence.models.Client;
import com.app.persistence.models.Product;
import com.app.service.exceptions.MarketServiceException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

    public Map<Client, List<Product>> init() {
        Map<Client, List<Product>> map = new HashMap<>();
        for (Client client : CLIENTS) {
            map.put(client, prepareUserBoughtProducts(client, userPreferences(client)));
        }
        return map;
    }

    public String showPurchases() {
        return purchases.entrySet()
                .stream()
                .map(e -> showClientInfo(e.getKey()) + "\nPRODCUCTS: " + e.getValue()
                        .stream()
                        .map(p -> p.getName() + " (" + p.getCategory() + "), price:" + p.getPrice())
                        .collect(Collectors.joining("\n"))
                )
                .collect(Collectors.joining("\n\n"));
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

    private double calculateProductRatio(Product p) {
        return p.getPrice().doubleValue() / p.getQuantity();
    }

    private List<Product> buyProductsFromCategory(Long id, BigDecimal cash) {
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