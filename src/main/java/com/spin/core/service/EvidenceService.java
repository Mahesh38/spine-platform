package com.spin.core.service;

import com.spin.core.audit.AuditEventType;
import com.spin.core.domain.EvidenceRecord;
import com.spin.core.engine.JsonUtil;
import com.spin.core.repo.CommitmentRepo;
import com.spin.core.repo.EvidenceRepo;
import com.spin.core.tenant.TenantContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@AllArgsConstructor
public class EvidenceService {

    private final EvidenceRepo evidenceRepo;
    private final CommitmentRepo commitmentRepo;
    private final AuditTrailService auditTrailService;
    private final JsonUtil jsonUtil;


    public EvidenceRecord addEvidence(String commitmentId, Map<String, Object> evidenceAttrs) {
        String json = jsonUtil.toJson(evidenceAttrs == null ? Map.of() : evidenceAttrs);
        String tenantId = TenantContext.getTenantId();
        commitmentRepo.findByTenantIdAndId(tenantId, commitmentId).orElseThrow();
        EvidenceRecord record = evidenceRepo.save(EvidenceRecord.builder()
                .tenantId(tenantId)
                .commitmentId(commitmentId)
                .attributesJson(json)
                .createdAt(Instant.now())
                .build());
        auditTrailService.log(
                AuditEventType.EVIDENCE_ADDED,
                null,
                commitmentId,
                "api",
                Map.of("evidenceId", record.getId(), "attributes", evidenceAttrs)
        );
        return record;
    }

}
