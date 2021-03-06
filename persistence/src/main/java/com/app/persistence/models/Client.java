package com.app.persistence.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Client {
    private String name;
    private String surname;
    private Integer age;
    private BigDecimal cash;
    private Long preferences;
}
