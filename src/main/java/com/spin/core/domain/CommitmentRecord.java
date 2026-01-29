package com.spin.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "commitments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommitmentRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String ownerEntityId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CommitmentState state;

  @Lob
  @Column(nullable = false)
  private String attributesJson; // dynamic attributes like dueAt, amount, etc.

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;
}
