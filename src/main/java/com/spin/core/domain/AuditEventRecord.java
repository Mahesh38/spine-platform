package com.spin.core.domain;

import com.spin.core.audit.AuditEventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "audit_events", indexes = {
        @Index(name = "idx_audit_tenant_commitment", columnList = "tenant_id,commitment_id"),
        @Index(name = "idx_audit_tenant_created", columnList = "tenant_id,created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEventRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditEventType eventType;

    // Optional: may be null for tenant-level/system events
    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "commitment_id")
    private String commitmentId;

    // Optional: user/service identity (for now can be "system" or "api")
    @Column(name = "actor")
    private String actor;

    // Optional: request correlation id (future)
    @Column(name = "correlation_id")
    private String correlationId;

    @Lob
    @Column(name = "payload_json", nullable = false)
    private String payloadJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
