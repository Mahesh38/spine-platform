package com.spin.core.engine;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SpineConfig {
  private List<String> states;
  private String initial;
  private Map<String, List<String>> transitions;
  private List<RuleDef> rules;

  @Data
  public static class RuleDef {
    private String name;
    private String when; // SpEL boolean
    private String then; // SpEL action -> must call #A.setState("STATE")
  }
}
