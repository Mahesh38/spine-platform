package com.spin.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "entities", indexes = {
        @Index(name = "idx_entity_tenant_id", columnList = "tenant_id,id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String type; // Company, Person, Role, etc.

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Instant createdAt;
}
