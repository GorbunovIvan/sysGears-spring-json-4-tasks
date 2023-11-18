package com.example.task2QueryForSortingAndSelecting.model.conditions;

import com.example.task2QueryForSortingAndSelecting.model.Table;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ConditionIncluding implements Condition {

    // Key is the name of a field in the table,
    // Value is a collection of values as conditions for that field.
    private Map<String, List<Object>> fields;

    @Override
    public void setValue(@Nonnull String json) {

        var typeReference = new TypeReference<List<Map<String, Object>>>() {};
        var fieldsAsListOfMaps = Condition.readConditionValueFromJson(json, typeReference);

        fields = fieldsAsListOfMaps.stream()
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    @Override
    public void applyToTable(@Nonnull Table table) {

        if (fields.isEmpty()) {
            return;
        }
        if (table.isEmpty()) {
            return;
        }

        var rowsToRemove = new ArrayList<Map<String, Object>>();

        for (var row : table) {
            for (var field : fields.entrySet()) {

                String conditionColumnName = field.getKey();
                List<Object> conditionValues = field.getValue();

                var valueInRow = row.get(conditionColumnName);
                if (valueInRow == null) {
                    throw new RuntimeException("Row has not field by name '" + conditionColumnName + "'");
                }

                var rowHasOneOfValuesFromCondition = conditionValues.contains(valueInRow);

                if (!rowHasOneOfValuesFromCondition) {
                    rowsToRemove.add(row);
                }
            }
        }

        for (var rowToRemove : rowsToRemove) {
            table.removeRow(rowToRemove);
        }
    }
}
