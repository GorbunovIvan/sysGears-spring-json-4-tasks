package com.example.task3Questionnaire.model;

import lombok.*;

import java.util.*;

@Getter @Setter
@EqualsAndHashCode
@ToString
public class ResultOfTestScript {

    private final Map<String, Object> paths;

    public ResultOfTestScript(List<List<QuestionAndAnswer>> scenarios) {
        var scenariosShortened = shortenScenarios(scenarios);
        paths = new HashMap<>();
        paths.put("number", scenariosShortened.size());
        paths.put("list", scenariosShortened);
    }

    private Set<Set<Map.Entry<String, String>>> shortenScenarios(List<List<QuestionAndAnswer>> scenarios) {
        var set = new HashSet<Set<Map.Entry<String, String>>>();
        for (var scenario : scenarios) {
            var scenarioEntries = new LinkedHashSet<Map.Entry<String, String>>();
            for (int i = 0; i < scenario.size()-1; i++) {
                var questionAndAnswer = scenario.get(i);
                var entry = new AbstractMap.SimpleEntry<>(questionAndAnswer.getQuestion().getContent(), questionAndAnswer.getAnswer());
                scenarioEntries.add(entry);
            }
            var lastQuestionAndAnswer = scenario.get(scenario.size()-1);
            var lastQuestion = lastQuestionAndAnswer.getQuestion();
            var entry = new AbstractMap.SimpleEntry<>(lastQuestion.getContent(), String.join(" / ", lastQuestion.getPossibleAnswers()));
            scenarioEntries.add(entry);
            set.add(scenarioEntries);
        }
        return set;
    }
}
