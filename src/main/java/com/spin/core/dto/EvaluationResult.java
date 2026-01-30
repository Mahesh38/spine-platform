package com.spin.core.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EvaluationResult {
  String nextState;          // null means "no change"
  String matchedRuleName;    // null means no rule matched
  Integer matchedRuleIndex;  // null means no rule matched
  String matchedWhen;        // optional (helps debug)
}
