package com.app.persistence.models;

import com.app.persistence.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Product {
    private String name;
    private int quantity;
    private BigDecimal price;
    private Category category;
}
