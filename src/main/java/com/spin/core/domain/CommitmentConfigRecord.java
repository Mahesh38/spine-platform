package com.spin.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "commitment_configs",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cfg_tenant_commitment", columnNames = {"tenant_id", "commitment_id"})
        },
        indexes = {
                @Index(name = "idx_cfg_tenant_commitment", columnList = "tenant_id,commitment_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommitmentConfigRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String commitmentId;

    @Lob
    @Column(nullable = false)
    private String configJson;

    @Column(nullable = false)
    private Instant createdAt;
}
