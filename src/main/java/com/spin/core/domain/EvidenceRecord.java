package com.spin.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "evidence", indexes = {
        @Index(name = "idx_evidence_tenant_id", columnList = "tenant_id,id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String commitmentId;

    @Lob
    @Column(nullable = false)
    private String attributesJson; // dynamic evidence fields like kind, utr, fileName

    @Column(nullable = false)
    private Instant createdAt;
}
