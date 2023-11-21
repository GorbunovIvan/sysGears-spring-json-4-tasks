package com.example.task3Questionnaire.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
public final class QuestionAndAnswer {

    private Question question;
    private String answer;

    public QuestionAndAnswer(@JsonProperty("question-content") String question, String answer) {
        this.question = new Question();
        this.question.setContent(question);
        this.answer = answer;
    }

    @JsonProperty("question-content")
    public String questionContent() {
        return Objects.requireNonNullElse(question.getContent(), null);
    }

    @Override
    public String toString() {
        return "QuestionAndAnswer{" +
                "question=" + question.getContent() +
                ", answer='" + answer + '\'' +
                '}';
    }
}
