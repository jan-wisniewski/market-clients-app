package com.app.persistence.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Client {
    private String name;
    private String surname;
    private int age;
    private BigDecimal cash;
    private long preferences;
}
