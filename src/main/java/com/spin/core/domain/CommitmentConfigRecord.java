package com.spin.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "commitment_configs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommitmentConfigRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, unique = true)
  private String commitmentId;

  @Lob
  @Column(nullable = false)
  private String configJson;

  @Column(nullable = false)
  private Instant createdAt;
}
