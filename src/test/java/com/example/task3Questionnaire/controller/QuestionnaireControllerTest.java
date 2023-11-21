package com.example.task3Questionnaire.controller;

import com.example.task3Questionnaire.model.QuestionAndAnswer;
import com.example.task3Questionnaire.model.Questionnaire;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionnaireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseURI = "/api/v1/questionnaire";

    @ParameterizedTest
    @ValueSource(strings = { "questionnaire1.json", "questionnaire2.json" })
    void testFullScenarios(String filename) throws Exception {

        var questionnaire = getQuestionnaire1(filename);
        var firstQuestion = questionnaire.getFirstQuestion();

        var jsonRequest = getQuestionnaire1InJson(filename);

        mockMvc.perform(post(baseURI + "/apply-questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string(firstQuestion.toString()));

        List<List<QuestionAndAnswer>> scenarios = questionnaire.getCollectionOfAllScenarios();

        for (var scenario : scenarios) {
            for (int i = 0; i < scenario.size(); i++) {

                var questionAndAnswer = scenario.get(i);
                var answerInJson = objectMapper.writeValueAsString(questionAndAnswer);

                var response = mockMvc.perform(post(baseURI + "/answer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(answerInJson))
                        .andExpect(status().isOk());

                if (i == scenario.size()-1) {

                    response.andExpect(content().string("No more questions"));

                    // This is the last question, so we reset the conversation
                    // to start a new scenario from scratch
                    mockMvc.perform(post(baseURI + "/reset")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonRequest))
                            .andExpect(status().isOk())
                            .andExpect(content().string(firstQuestion.toString()));

                } else {
                    // Checking to see if it returned the correct next question
                    var nextQuestionAndAnswer = scenario.get(i+1);
                    var nextQuestion = nextQuestionAndAnswer.getQuestion();
                    response.andExpect(content().string(nextQuestion.toString()));
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "questionnaire1.json", "questionnaire2.json" })
    void testGetResultOfTestScript(String filename) throws Exception {

        var jsonRequest = getQuestionnaire1InJson(filename);

        mockMvc.perform(post(baseURI + "/test-script")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    private String getQuestionnaire1InJson(String fileName) {

        var resource = new ClassPathResource(fileName);

        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file '" + fileName + "' from resources", e);
        }
    }

    private Questionnaire getQuestionnaire1(String fileName) {
        var resource = new ClassPathResource(fileName);
        try {
            return objectMapper.readValue(resource.getInputStream(), Questionnaire.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file '" + fileName + "' from resources", e);
        }
    }
}