package com.example.task3Questionnaire.controller;

import com.example.task3Questionnaire.model.QuestionAndAnswer;
import com.example.task3Questionnaire.model.Questionnaire;
import com.example.task3Questionnaire.model.ResultOfTestScript;
import com.example.task3Questionnaire.service.QuestionnaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questionnaire")
@RequiredArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    /**
     * Receives and applies the provided questionnaire (config that describes all the scenarios)
     * and returns the first question
     * The example looks like:
     * <p><blockquote><pre>
     * {
     *   "firstQuestion":{
     *     "content":"What is your marital status?",
     *     "possibleAnswers":["Single", "Married"]
     *   },
     *   "questions":[
     *     {
     *       "previousQuestionAndAnswer":{
     *         "question-content":"What is your marital status?",
     *         "answer":"Single"
     *       },
     *       "content":"Are you planning on getting married next year?",
     *       "possibleAnswers":["Yes", "No"]
     *     },
     *     {
     *       "previousQuestionAndAnswer":{
     *         "question-content":"What is your marital status?",
     *         "answer":"Married"
     *       },
     *       "content":"How long have you been married?",
     *       "possibleAnswers":["Less than a year", "More than a year"]
     *     },
     *     {
     *       "previousQuestionAndAnswer":{
     *         "question-content":"How long have you been married?",
     *         "answer":"More than a year"
     *       },
     *       "content":"Have you celebrated your one year anniversary?",
     *       "possibleAnswers":["Yes", "No"]
     *     }
     *   ]
     * }
     * </pre></blockquote></p>
     * @param questionnaire The questionnaire object that describes all the scenarios
     * @return The first question.
     */
    @PostMapping("/apply-questions")
    public ResponseEntity<String> applyQuestions(@RequestBody Questionnaire questionnaire) {
        questionnaireService.applyNewQuestionnaire(questionnaire);
        var nextQuestion = questionnaire.getFirstQuestion();
        if (nextQuestion == null) {
            return ResponseEntity.ok("No questions");
        }
        return ResponseEntity.ok(nextQuestion.toString());
    }

    /**
     * Receives a question and an answer to it and returns the next question based on this answer
     * @param questionAndAnswer An object with a question and an answer to it
     * @return The next question based on provided answer
     */
    @PostMapping("/answer")
    public ResponseEntity<String> answer(@RequestBody QuestionAndAnswer questionAndAnswer) {
        var nextQuestion = questionnaireService.answer(questionAndAnswer);
        if (nextQuestion == null) {
            return ResponseEntity.ok("No more questions");
        }
        return ResponseEntity.ok(nextQuestion.toString());
    }

    /**
     * Clears the conversation history and returns the first question
     * @return The first question
     */
    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        questionnaireService.reset();
        var nextQuestion = questionnaireService.getNextQuestion();
        if (nextQuestion == null) {
            return ResponseEntity.ok("No more questions");
        }
        return ResponseEntity.ok(nextQuestion.toString());
    }

    /**
     * Receives and tests the provided questionnaire (config that describes all the scenarios)
     * and returns the test results
     * @param questionnaire The questionnaire object that describes all the scenarios
     * @return The test results.
     */
    @PostMapping("/test-script")
    public ResponseEntity<ResultOfTestScript> getResultOfTestScript(@RequestBody Questionnaire questionnaire) {
        var result = questionnaireService.getResultOfTestScript(questionnaire);
        return ResponseEntity.ok(result);
    }
}
