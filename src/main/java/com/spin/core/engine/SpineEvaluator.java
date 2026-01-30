package com.spin.core.engine;

import com.spin.core.dto.EvaluationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SpineEvaluator {

    private final SpelRuleEngine spelRuleEngine;

    public EvaluationResult evaluateNextState(SpineConfig cfg,
                                    String currentState,
                                    Map<String, Object> commitmentAttrs,
                                    Set<String> evidenceKinds,
                                    int evidenceCount,
                                    Instant now) {

        EvaluationResult proposed = spelRuleEngine.proposeNextState(
                cfg, currentState, commitmentAttrs, evidenceKinds, evidenceCount, now
        );

        EvaluationResult noChange = EvaluationResult.builder().nextState(null).build();
        if (proposed == null || proposed.getNextState() == null || currentState.equals(proposed.getNextState())) {
            return noChange;
        }

        List<String> allowed = (cfg.getTransitions() == null)
                ? List.of()
                : cfg.getTransitions().getOrDefault(currentState, List.of());

        if (!allowed.contains(proposed.getNextState()))
            return noChange;

        return proposed;
    }
}
