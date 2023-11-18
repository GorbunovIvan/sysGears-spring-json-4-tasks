package com.example.task2QueryForSortingAndSelecting.controller;

import com.example.task2QueryForSortingAndSelecting.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    /**
     * Receives the table and conditions to apply to it from json
     * @param json The table and conditions in json (look tests for this controller)
     * @return Table in json with conditions applied
     */
    @PostMapping
    public ResponseEntity<String> applyConditionsOnData(@RequestBody String json) {
        var jsonResult = tableService.handleTableAndConditionsFromJsonAndReturnJson(json);
        return ResponseEntity.ok(jsonResult);
    }
}
