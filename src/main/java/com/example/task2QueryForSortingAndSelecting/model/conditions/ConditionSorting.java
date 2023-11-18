package com.example.task2QueryForSortingAndSelecting.model.conditions;

import com.example.task2QueryForSortingAndSelecting.model.Table;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ConditionSorting implements Condition {

    private List<String> columns;

    @Override
    public void setValue(@Nonnull String json) {
        var typeReference = new TypeReference<List<String>>() {};
        columns = Condition.readConditionValueFromJson(json, typeReference);
    }

    @Override
    public void applyToTable(@Nonnull Table table) {
        table.sort(columns);
    }
}
