package com.example.task3Questionnaire.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Questionnaire {

    private Question firstQuestion;
    private List<Question> questions;

    @JsonIgnore
    public Optional<Question> getNextQuestion(QuestionAndAnswer questionAndAnswer) {
        return getQuestions().stream()
                .filter(q -> q.getPreviousQuestionAndAnswer().equals(questionAndAnswer))
                .findFirst();
    }

    @JsonIgnore
    public Optional<Question> findQuestionByContent(String content) {

        var questionOpt = questions.stream()
                .filter(q -> q.getContent().equals(content))
                .findAny();

        if (questionOpt.isEmpty()) {
            if (firstQuestion.getContent().equals(content)) {
                questionOpt = Optional.of(firstQuestion);
            }
        }

        return questionOpt;
    }

    public List<List<QuestionAndAnswer>> getCollectionOfAllScenarios() {

        var scenarios = new ArrayList<List<QuestionAndAnswer>>();
        var question = getFirstQuestion();
        for (var answer : question.getPossibleAnswers()) {
            var questionAndAnswer = new QuestionAndAnswer(question, answer);
            walkByScenarios(questionAndAnswer, scenarios, new ArrayList<>());
        }

        // I leave it commented instead of removing because it is very precious
//        for (var scenario : scenarios) {
//            System.out.println("[");
//            for (var questionAndAnswer : scenario) {
//                System.out.println("    " + questionAndAnswer);
//            }
//            System.out.println("[");
//        }

        return scenarios;
    }

    private void walkByScenarios(QuestionAndAnswer questionAndAnswer, List<List<QuestionAndAnswer>> scenarios, List<QuestionAndAnswer> scenario) {

        scenario.add(questionAndAnswer);

        Optional<Question> nextQuestionOpt = getNextQuestion(questionAndAnswer);
        if (nextQuestionOpt.isEmpty()) {
            scenarios.add(scenario);
            return;
        }

        var nextQuestion = nextQuestionOpt.get();

        for (var answer : nextQuestion.getPossibleAnswers()) {
            var nextQuestionAndAnswer = new QuestionAndAnswer(nextQuestion, answer);
            walkByScenarios(nextQuestionAndAnswer, scenarios, new ArrayList<>(scenario));
        }
    }
}
