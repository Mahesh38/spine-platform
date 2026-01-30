package com.spin.core.service;

import com.spin.core.audit.AuditEventType;
import com.spin.core.domain.CommitmentConfigRecord;
import com.spin.core.domain.CommitmentRecord;
import com.spin.core.domain.CommitmentState;
import com.spin.core.domain.EvidenceRecord;
import com.spin.core.dto.EvaluationResult;
import com.spin.core.engine.JsonUtil;
import com.spin.core.engine.SpineConfig;
import com.spin.core.engine.SpineEvaluator;
import com.spin.core.repo.CommitmentConfigRepo;
import com.spin.core.repo.CommitmentRepo;
import com.spin.core.repo.EvidenceRepo;
import com.spin.core.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommitmentService {

    private final EvaluationOrchestrator evaluationOrchestrator;
    private final CommitmentRepo commitmentRepo;
    private final AuditTrailService auditTrailService;
    private final JsonUtil jsonUtil;

    public CommitmentRecord create(String title, String ownerEntityId, Map<String, Object> attributes) {
        String attrsJson = jsonUtil.toJson(attributes == null ? Map.of() : attributes);

        CommitmentRecord record = commitmentRepo.save(CommitmentRecord.builder()
                .title(title)
                .tenantId(TenantContext.getTenantId())
                .ownerEntityId(ownerEntityId)
                .state(CommitmentState.CREATED)
                .attributesJson(attrsJson)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());
        auditTrailService.log(
                AuditEventType.COMMITMENT_CREATED,
                null,
                record.getId(),
                "api",
                Map.of("title", record.getTitle(), "ownerEntityId", record.getOwnerEntityId(), "attributes", attributes)
        );
        return record;
    }

    public CommitmentRecord activate(String id) {
        String tenantId = TenantContext.getTenantId();
        CommitmentRecord c = commitmentRepo.findByTenantIdAndId(tenantId, id).orElseThrow();
        CommitmentState oldState = c.getState();
        c.setState(CommitmentState.ACTIVE);
        c.setUpdatedAt(Instant.now());
        c = commitmentRepo.save(c);
        auditTrailService.log(
                AuditEventType.COMMITMENT_ACTIVATED,
                null,
                c.getId(),
                "api",
                Map.of("oldState", oldState, "newState", c.getState())
        );
        return c;
    }

    public CommitmentRecord evaluate(String commitmentId) {
        return evaluationOrchestrator.evaluateCommitment(commitmentId);
    }

    public CommitmentRecord get(String id) {
        String tenantId = TenantContext.getTenantId();
        return commitmentRepo.findByTenantIdAndId(tenantId, id).orElseThrow();
    }
}
