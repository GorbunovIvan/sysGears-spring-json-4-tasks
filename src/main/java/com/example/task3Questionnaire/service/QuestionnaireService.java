package com.example.task3Questionnaire.service;

import com.example.task3Questionnaire.model.Question;
import com.example.task3Questionnaire.model.QuestionAndAnswer;
import com.example.task3Questionnaire.model.Questionnaire;
import com.example.task3Questionnaire.model.ResultOfTestScript;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Getter
public class QuestionnaireService {

    private Questionnaire questionnaire;

    private final List<QuestionAndAnswer> conversationHistory = new ArrayList<>();

    public void applyNewQuestionnaire(Questionnaire questionnaire) {
        validateQuestionnaire(questionnaire);
        conversationHistory.clear();
        this.questionnaire = questionnaire;
    }

    private void validateQuestionnaire(@Nonnull Questionnaire questionnaire) {

        // Checking the first question
        var firstQuestion = questionnaire.getFirstQuestion();
        if (firstQuestion == null) {
            throw new RuntimeException("The first question was not found");
        }
        if (firstQuestion.getPossibleAnswers().isEmpty()) {
            throw new RuntimeException("No answers were found for the first question");
        }

        // Checking all questions for answers
        for (var question : questionnaire.getQuestions()) {
            if (question.getPossibleAnswers().isEmpty()) {
                throw new RuntimeException("No answers were found for the question:\n" +
                        "'" + question + "'");
            }
        }

        // Checking the uniqueness of the questions
        var distinctQuestions = new HashSet<>(questionnaire.getQuestions());
        if (distinctQuestions.size() < questionnaire.getQuestions().size()) {
            throw new RuntimeException("There are the same questions");
        }
        if (questionnaire.getQuestions().contains(firstQuestion)) {
            throw new RuntimeException("There are questions that are similar to the first question");
        }

        // Check for the existence of a question specified as a trigger for other questions
        for (var question : questionnaire.getQuestions()) {

            var previousQuestionAndAnswer = question.getPreviousQuestionAndAnswer();

            var previousQuestion = previousQuestionAndAnswer.getQuestion();
            if (!questionnaire.getQuestions().contains(previousQuestion)
                    && !previousQuestion.equals(firstQuestion)) {
                throw new RuntimeException("The question has the previous question to be triggered that does not exists:\n" +
                        "'" + question + "'");
            }

            // Validating the answer
            validateQuestionAndAnswer(questionnaire, previousQuestionAndAnswer);
        }

        // Checking whether there are any questions that should trigger the same answer
        // to the previous question
        for (var question1 : questionnaire.getQuestions()) {
            var previousQuestionAndAnswer1 = question1.getPreviousQuestionAndAnswer();
            for (var question2 : questionnaire.getQuestions()) {
                if (question1.equals(question2)) {
                    continue;
                }
                if (question2.getPreviousQuestionAndAnswer().equals(previousQuestionAndAnswer1)) {
                    throw new RuntimeException("The are questions that has the same previous question and answer to be triggered:\n" +
                            "'" + previousQuestionAndAnswer1 + "'");
                }
            }
        }
    }

    public Question answer(@Nonnull QuestionAndAnswer questionAndAnswer) {

        questionAndAnswer = validateQuestionAndAnswer(questionAndAnswer);

        var expectedQuestion = getNextQuestion();
        if (expectedQuestion == null) {
            throw new RuntimeException("You're answering the wrong question." +
                    "There are no more questions to answer." +
                    "If you want to start the conversation again, you can reset it with .../api/v1/rest");
        }

        if (!expectedQuestion.equals(questionAndAnswer.getQuestion())) {
            throw new RuntimeException("You're answering the wrong question. " +
                    "The correct question is '" + expectedQuestion + "'." +
                    "Or you can reset conversation on .../api/v1/rest");
        }

        conversationHistory.add(questionAndAnswer);
        return getNextQuestion(questionAndAnswer);
    }

    private QuestionAndAnswer validateQuestionAndAnswer(QuestionAndAnswer questionAndAnswer) {
        return validateQuestionAndAnswer(this.questionnaire, questionAndAnswer);
    }

    private QuestionAndAnswer validateQuestionAndAnswer(@Nonnull Questionnaire questionnaire, @Nonnull QuestionAndAnswer questionAndAnswer) {

        var questionContent = questionAndAnswer.getQuestion().getContent();
        var questionOpt = questionnaire.findQuestionByContent(questionContent);

        if (questionOpt.isEmpty()) {
            throw new RuntimeException("Question is not found: '" + questionContent + "'");
        }

        var question = questionOpt.get();
        var answer = questionAndAnswer.getAnswer();

        if (!question.isAnswerValid(questionAndAnswer.getAnswer())) {
            throw new RuntimeException("Answer '" + answer + "' is not found for question '" + question.getContent() + "'." +
                    "Possible answers are: '" + question.getPossibleAnswers() + "'");
        }

        return new QuestionAndAnswer(question, answer);
    }

    public Question getNextQuestion() {
        if (conversationHistory.isEmpty()) {
            return questionnaire.getFirstQuestion();
        }
        var lastQuestionAndAnswer = getLastQuestionAndAnswer();
        return getNextQuestion(lastQuestionAndAnswer);
    }

    public Question getNextQuestion(QuestionAndAnswer question) {
        if (question == null) {
            return null;
        }
        var nextQuestionOpt = questionnaire.getNextQuestion(question);
        return nextQuestionOpt.orElse(null);
    }

    public QuestionAndAnswer getLastQuestionAndAnswer() {
        if (conversationHistory.isEmpty()) {
            return null;
        }
        return conversationHistory.get(conversationHistory.size()-1); // The last
    }

    public void reset() {
        conversationHistory.clear();
    }

    public ResultOfTestScript getResultOfTestScript(@Nonnull Questionnaire questionnaire) {
        validateQuestionnaire(questionnaire);
        var scenarios = questionnaire.getCollectionOfAllScenarios();
        return new ResultOfTestScript(scenarios);
    }
}
