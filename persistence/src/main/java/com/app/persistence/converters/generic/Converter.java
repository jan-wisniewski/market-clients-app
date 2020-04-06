package com.app.persistence.converters.generic;

import java.util.List;

public interface Converter<U> {
    List<U> convert();
}
