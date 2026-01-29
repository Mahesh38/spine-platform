package com.spin.core.engine;

import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

@Component
public class JsonUtil {

  private final ObjectMapper mapper = new ObjectMapper();

  public Map<String, Object> toMap(String json) {
    if (json == null || json.isBlank()) return Collections.emptyMap();
    try {
      return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JSON: " + e.getMessage(), e);
    }
  }

  public String toJson(Map<String, Object> map) {
    try {
      return mapper.writeValueAsString(map);
    } catch (Exception e) {
      throw new IllegalArgumentException("Cannot serialize JSON: " + e.getMessage(), e);
    }
  }

  public SpineConfig toConfig(String json) {
    try {
      return mapper.readValue(json, SpineConfig.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid config JSON: " + e.getMessage(), e);
    }
  }
}
