package com.spin.core.service;

import com.spin.core.audit.AuditEventType;
import com.spin.core.domain.AuditEventRecord;
import com.spin.core.engine.JsonUtil;
import com.spin.core.repo.AuditEventRepo;
import com.spin.core.tenant.RequestContext;
import com.spin.core.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditTrailService {

    private final AuditEventRepo repo;
    private final JsonUtil jsonUtil;

    public void log(AuditEventType type,
                    String entityId,
                    String commitmentId,
                    String actor,
                    Map<String, Object> payload) {

        try {
            String tenantId = TenantContext.getTenantId();
            String correlationId = RequestContext.getRequestId();

            Map<String, Object> enriched = new java.util.LinkedHashMap<>();
            enriched.put("requestId", correlationId);
            enriched.put("eventType", type.name());
            enriched.put("actor", actor == null ? "system" : actor);
            if (payload != null) enriched.putAll(payload);

            repo.save(AuditEventRecord.builder()
                    .tenantId(tenantId)
                    .correlationId(correlationId)
                    .eventType(type)
                    .entityId(entityId)
                    .commitmentId(commitmentId)
                    .actor(actor == null ? "system" : actor)
                    .payloadJson(jsonUtil.toJson(enriched))
                    .createdAt(Instant.now())
                    .build());
        } catch (Exception e) {
            log.error("Audit must never break the business flow. Error while audit log!", e);
        }
    }
}
