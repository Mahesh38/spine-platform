package com.spin.core.engine;

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

  public String evaluateNextState(SpineConfig cfg,
                                  String currentState,
                                  Map<String, Object> commitmentAttrs,
                                  Set<String> evidenceKinds,
                                  int evidenceCount,
                                  Instant now) {

    String proposed = spelRuleEngine.proposeNextState(
        cfg, currentState, commitmentAttrs, evidenceKinds, evidenceCount, now
    );

    if (proposed == null || proposed.equals(currentState)) return currentState;

    List<String> allowed = (cfg.getTransitions() == null)
        ? List.of()
        : cfg.getTransitions().getOrDefault(currentState, List.of());

    if (!allowed.contains(proposed)) return currentState;

    return proposed;
  }
}
