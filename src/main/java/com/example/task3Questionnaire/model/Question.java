package com.example.task3Questionnaire.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "content")
public class Question {

    private String content;

    private List<String> possibleAnswers;

    private QuestionAndAnswer previousQuestionAndAnswer;

    public Question(String content) {
        this.content = content;
    }

    @JsonIgnore
    public boolean isAnswerValid(String answer) {
        return possibleAnswers.contains(answer);
    }

    @Override
    public String toString() {
        return "question = '" + content + '\'' +
                ", answers = '" + (possibleAnswers == null ? null : String.join("' / '", possibleAnswers)) + '\'';
    }
}
