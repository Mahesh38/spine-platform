package com.spin.core.service;

import com.spin.core.audit.AuditEventType;
import com.spin.core.domain.EntityRecord;
import com.spin.core.repo.EntityRepo;
import com.spin.core.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EntityService {

    private final EntityRepo entityRepo;
    private final AuditTrailService auditTrailService;

    public EntityRecord create(String type, String name) {
        EntityRecord entity = entityRepo.save(EntityRecord.builder()
                .tenantId(TenantContext.getTenantId())
                .type(type)
                .name(name)
                .createdAt(Instant.now())
                .build());
        auditTrailService.log(
                AuditEventType.ENTITY_CREATED,
                entity.getId(),
                null,
                "api",
                Map.of("type", entity.getType(), "name", entity.getName())
        );
        return entity;
    }
}
