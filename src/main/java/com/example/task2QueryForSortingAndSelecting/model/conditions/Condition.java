package com.example.task2QueryForSortingAndSelecting.model.conditions;

import com.example.task2QueryForSortingAndSelecting.model.Table;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;

public interface Condition {

    ObjectMapper objectMapper = new ObjectMapper();

    void setValue(@Nonnull String json);
    void applyToTable(@Nonnull Table table);

    static Condition createConditionByName(@Nonnull String name) {
        if (name.equalsIgnoreCase("include")) {
            return new ConditionIncluding();
        }
        if (name.equalsIgnoreCase("exclude")) {
            return new ConditionExcluding();
        }
        if (name.equalsIgnoreCase("sort_by")) {
            return new ConditionSorting();
        }
        return null;
    }

    static <T> T readConditionValueFromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
