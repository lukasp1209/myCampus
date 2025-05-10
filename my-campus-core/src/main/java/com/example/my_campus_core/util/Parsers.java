package com.example.my_campus_core.util;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class Parsers {

    public List<Integer> parseIgnoreIds(String ignoreIds) {
        if (ignoreIds == null || ignoreIds.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return Stream.of(ignoreIds.split(","))
                    .map(String::trim)
                    .filter(id -> !id.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            // Log the error if needed, but return empty list to avoid breaking the API
            return Collections.emptyList();
        }
    }
}
