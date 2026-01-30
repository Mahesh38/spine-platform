package com.spin.core.service;

import com.spin.core.dto.EvaluationFacts;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FactBuilder {

    public EvaluationFacts build(String currentState,
                                 Map<String, Object> commitmentAttrs,
                                 List<Map<String, Object>> evidenceMaps,
                                 Instant now){

        Set<String> evidenceKinds = evidenceMaps.stream()
                .map(m -> String.valueOf(m.getOrDefault("kind", "")))
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toSet());

        int evidenceCount = evidenceMaps.size();

        Map<String, Object> factsSummary = Map.of(
                "currentState", currentState,
                "evidenceKinds", evidenceKinds,
                "evidenceCount", evidenceCount
        );

        return EvaluationFacts.builder()
                .currentState(currentState)
                .now(now)
                .commitment(commitmentAttrs)
                .evidenceKinds(evidenceKinds)
                .evidenceCount(evidenceCount)
                .summary(factsSummary)
                .build();

    }


}
