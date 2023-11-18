package com.example.task2QueryForSortingAndSelecting.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String baseURI = "/api/v1/tables";

    @Test
    void testApplyConditionsOnDataWithIncluding() throws Exception {

        var jsonRequest = """
                {"data": [
                  {"name": "John", "email": "john2@mail.com"},
                  {"name": "John", "email": "john1@mail.com"},
                  {"name": "Jane", "email": "jane@mail.com"}
                ],
                "condition": {
                  "include": [{"name": "John"}]
                }}""";

        var jsonExpected = """
                {"result": [
                  {"name": "John", "email": "john2@mail.com"},
                  {"name": "John", "email": "john1@mail.com"}
                ]}""";

        mockMvc.perform(post(baseURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
    }
    @Test
    void testApplyConditionsOnDataWithIncludingAndBasicSorting() throws Exception {

        var jsonRequest = """
                {"data": [
                  {"name": "John", "email": "john2@mail.com"},
                  {"name": "John", "email": "john1@mail.com"},
                  {"name": "Jane", "email": "jane@mail.com"}
                ],
                "condition": {
                  "include": [{"name": "John"}],
                  "sort_by": ["email"]
                }}""";

        var jsonExpected = """
                {"result": [
                  {"name": "John", "email": "john1@mail.com"},
                  {"name": "John", "email": "john2@mail.com"}
                ]}""";

        mockMvc.perform(post(baseURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
    }

    @Test
    void testApplyConditionsOnDataWithAdvancedSorting() throws Exception {

        var jsonRequest = """
                {"data": [
                  {"name": "John", "email": "john2@mail.com"},
                  {"name": "John", "email": "john1@mail.com"},
                  {"name": "Bob",  "email": "john1@mail.com"},
                  {"name": "Steve", "email": "john1@mail.com"},
                  {"name": "Jane", "email": "jane@mail.com"}
                ],
                "condition": {
                  "sort_by": ["email", "name"]
                }}""";

        var jsonExpected = """
                {"result": [
                  {"name": "Jane", "email": "jane@mail.com"},
                  {"name": "Bob",  "email": "john1@mail.com"},
                  {"name": "John", "email": "john1@mail.com"},
                  {"name": "Steve", "email": "john1@mail.com"},
                  {"name": "John", "email": "john2@mail.com"}
                ]}""";

        mockMvc.perform(post(baseURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
    }
    @Test
    void testApplyConditionsOnDataWithExcludingAndBasicSorting() throws Exception {

        var jsonRequest = """
                {"data": [
                  {"user": "mike@mail.com", "rating": 20, "disabled": false},
                  {"user": "greg@mail.com", "rating": 14, "disabled": false},
                  {"user": "john@mail.com", "rating": 25, "disabled": true}
                ],
                "condition": {
                  "exclude": [{"disabled": true}],
                  "sort_by": ["rating"]
                }}""";

        var jsonExpected = """
                {"result": [
                  {"user": "greg@mail.com", "rating": 14, "disabled": false},
                  {"user": "mike@mail.com", "rating": 20, "disabled": false}
                ]}""";

        mockMvc.perform(post(baseURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
    }
}