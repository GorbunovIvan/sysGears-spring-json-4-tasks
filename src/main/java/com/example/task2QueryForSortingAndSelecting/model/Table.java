package com.example.task2QueryForSortingAndSelecting.model;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@Slf4j
public class Table implements Iterable<Map<String, Object>> {

    private List<Map<String, Object>> records;

    private final static int FIELD_LENGTH_IN_FORMATTING = 20;

    public List<String> getColumns() {
        return getRecords().stream()
                .map(Map::keySet)
                .flatMap(Set::stream)
                .distinct()
                .sorted()
                .toList();
    }

    public void removeRow(Map<String, Object> row) {
        records.remove(row);
    }

    public int getSize() {
        return records.size();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public void sort(@Nonnull List<String> columnsForSorting) {

        var columnsOfTable = getColumns();
        for (var column : columnsForSorting) {
            if (!columnsOfTable.contains(column)) {
                throw new RuntimeException("Table has not a column '" + column + "' (while trying to sort)");
            }
        }

        if (columnsForSorting.isEmpty()) {
            return;
        }
        if (records.isEmpty()) {
            return;
        }

        List<Map<String, Object>> recordsCurrent = new ArrayList<>(records);

        records.sort((row1, row2) -> {
            int compareResult = 0;
            for (var column : columnsForSorting) {
                if (compareResult == 0) {
                    var row1Value = row1.get(column);
                    var row2Value = row2.get(column);
                    compareResult = compareValues(row1Value, row2Value);
                }
            }
            return compareResult;
        });

        if (records.size() < recordsCurrent.size()) {
            recordsCurrent.removeAll(records);
            records.addAll(recordsCurrent);
        }
    }

    private int compareValues(Object row1Value, Object row2Value) {

        if (row1Value == null) {
            return row2Value == null ? 0 : -1;
        } else if (row2Value == null) {
            return 1;
        }

        var isRow1Comparable = row1Value instanceof Comparable;
        var isRow2Comparable = row2Value instanceof Comparable;

        if (!isRow1Comparable) {
            log.error("Value '{}' doesn't implement Comparable interface", row1Value);
            return isRow2Comparable ? -1 : 0;
        } else if (!isRow2Comparable) {
            log.error("Value '{}' doesn't implement Comparable interface", row2Value);
            return 1;
        }

        var result = compareTwoObjectsIfTheyHaveTheSameClass(row1Value, row2Value);
        if (result != null) {
            return result;
        }

        return 0;
    }

    private <T, K> Integer compareTwoObjectsIfTheyHaveTheSameClass(T obj1, K obj2) {
        Class<?> targetClass = obj1.getClass();
        if (targetClass.isInstance(obj2)) {
            try {
                var methodCompareTo = targetClass.getMethod("compareTo", targetClass);
                return (Integer) methodCompareTo.invoke(obj1, obj2);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {}
        }
        targetClass = obj2.getClass();
        if (targetClass.isInstance(obj1)) {
            try {
                var methodCompareTo = targetClass.getMethod("compareTo", targetClass);
                return (Integer) methodCompareTo.invoke(obj1, obj2);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {}
        }
        return null;
    }

    @Override
    @Nonnull
    public Iterator<Map<String, Object>> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<Map<String, Object>> {

        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < getRecords().size();
        }

        @Override
        public Map<String, Object> next() {
            if (!hasNext()) {
                throw new IllegalStateException();
            }
            return getRecords().get(currentIndex++);
        }
    }

    public String toString() {

        if (isEmpty()) {
            return "<empty table>";
        }

        var sb = new StringBuilder();

        // Columns
        var columns = getColumns();

        sb.append(" | ");
        for (var column : columns) {
            sb.append(formatField(column));
            sb.append(" | ");
        }
        sb.append("\n");

        // Horizontal line between columns and rows
        sb.append(" | ");
        for (var ignored : columns) {
            sb.append(formatField("-".repeat(FIELD_LENGTH_IN_FORMATTING)));
            sb.append(" | ");
        }
        sb.append("\n");

        // Rows
        for (var record : records) {
            sb.append(" | ");
            for (var column : columns) {
                var value = record.get(column);
                value = formatField(Objects.requireNonNullElse(value, ""));
                sb.append(value);
                sb.append(" | ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String formatField(Object value) {
        var str = String.valueOf(value);
        str = str.substring(0, Math.min(str.length(), FIELD_LENGTH_IN_FORMATTING));
        return String.format("%-" + FIELD_LENGTH_IN_FORMATTING + "s", str);
    }
}
