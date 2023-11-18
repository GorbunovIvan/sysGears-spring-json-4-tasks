package com.example.task1UnitsOfLength.controller;

import com.example.task1UnitsOfLength.model.Distance;
import com.example.task1UnitsOfLength.model.RequestDistance;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class ConverterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseURI = "/api/v1/converter";

    @Test
    void testConvertMToFt() throws Exception {

        var distance = new Distance("m", 0.5);
        var request = new RequestDistance(distance, "ft");

        var jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(baseURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("1,64"));
    }

    @Test
    void testConvertInToYd() throws Exception {

        var distance = new Distance("in", 27);
        var request = new RequestDistance(distance, "yd");

        var jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(baseURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("0,75"));
    }

    @Test
    void testConvertYdToIn() throws Exception {

        var distance = new Distance("yd", 54);
        var request = new RequestDistance(distance, "in");

        var jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(baseURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("1944"));
    }

    @Test
    void testConvertError() throws Exception {

        var distance = new Distance("error", 1);
        var request = new RequestDistance(distance, "error");

        var jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(baseURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isInternalServerError());
    }
}