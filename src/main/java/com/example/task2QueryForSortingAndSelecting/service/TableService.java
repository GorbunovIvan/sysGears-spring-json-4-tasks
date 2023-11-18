package com.example.task2QueryForSortingAndSelecting.service;

import com.example.task2QueryForSortingAndSelecting.model.conditions.Condition;
import com.example.task2QueryForSortingAndSelecting.model.Table;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TableService {

    private final ObjectMapper objectMapper;

    public String handleTableAndConditionsFromJsonAndReturnJson(String json) {
        var table = handleTableAndConditionsFromJson(json);
        return convertTableToJson(table);
    }

    private Table handleTableAndConditionsFromJson(String json) {

        JsonNode rootNode;

        try {
            rootNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        var dataNode = rootNode.get("data");
        var conditionNode = rootNode.get("condition");

        var table = createTableFromJsonNode(dataNode);
        applyConditionsToTheTable(table, conditionNode);

        return table;
    }

    private Table createTableFromJsonNode(JsonNode node) {

        if (node == null) {
            throw new RuntimeException("No 'data' attribute found in json.");
        }

        List<Map<String, Object>> records;

        try {
            String json = node.toString();
            records = objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to read 'data' attribute from json.", e);
        }

        return new Table(records);
    }

    private void applyConditionsToTheTable(Table table, JsonNode node) {

        var conditions = getConditionsFromJsonNode(node);

        conditions = conditions.stream().distinct().toList();

        for (var condition : conditions) {
            condition.applyToTable(table);
        }
    }

    private List<Condition> getConditionsFromJsonNode(JsonNode node) {

        if (node == null) {
            return Collections.emptyList();
        }

        String json = node.toString();

        JsonNode nodeConditions;

        try {
            nodeConditions = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<Condition> conditions = new ArrayList<>();

        var conditionsNames = nodeConditions.fieldNames();

        while (conditionsNames.hasNext()) {
            var conditionName = conditionsNames.next();
            var nodeCondition = nodeConditions.get(conditionName);
            if (nodeCondition == null) {
                log.error("Condition value {} is not found by its name", conditionName);
                continue;
            }
            var conditionObject = Condition.createConditionByName(conditionName);
            if (conditionObject == null) {
                log.error("Condition {} is not defined", conditionName);
                continue;
            }
            conditionObject.setValue(nodeCondition.toString());
            conditions.add(conditionObject);
        }

        return conditions;
    }

    private String convertTableToJson(Table table) {

        var jsonRoot = new JSONObject();

        for (var record : table) {
            var jsonRecord = new JSONObject();
            for (var field : record.entrySet()) {
                jsonRecord.put(field.getKey(), field.getValue());
            }
            jsonRoot.append("result", jsonRecord);
        }

        return jsonRoot.toString();
    }
}
