package com.spin.core.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Value
@Builder
public class EvaluationFacts {

    String currentState;
    Instant now;
    Map<String, Object> commitment;
    Set<String> evidenceKinds;
    int evidenceCount;
    Map<String, Object> summary;

}
