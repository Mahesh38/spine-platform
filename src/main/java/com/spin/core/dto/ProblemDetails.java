package com.spin.core.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemDetails {
  private String type;
  private String title;
  private int status;
  private String detail;
  private String code;
  private String instance;
  private String requestId;
}
