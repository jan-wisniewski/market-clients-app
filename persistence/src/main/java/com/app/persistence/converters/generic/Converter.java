package com.app.persistence.converters.generic;

import java.util.List;

public interface Converter <T> {
    List<T> convert();
}
