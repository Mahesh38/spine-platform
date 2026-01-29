package com.spin.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "entities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EntityRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String type; // Company, Person, Role, etc.

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Instant createdAt;
}
