package com.app.persistence.converters.impl;

import com.app.persistence.converters.generic.JsonConverter;
import com.app.persistence.models.Client;

import java.util.List;

public class ClientsJsonConverter extends JsonConverter<List<Client>> {
    public ClientsJsonConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
