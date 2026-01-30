package com.spin.core.service;

import com.spin.core.audit.AuditEventType;
import com.spin.core.domain.CommitmentConfigRecord;
import com.spin.core.engine.JsonUtil;
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
public class CommitmentConfigService {

    private final CommitmentConfigRepo configRepo;
    private final AuditTrailService auditTrailService;
    private final JsonUtil jsonUtil;


    public CommitmentConfigRecord configure(String commitmentId, String configJson) {
        // Validate config now
        jsonUtil.toConfig(configJson);
        String tenantId = TenantContext.getTenantId();
        CommitmentConfigRecord existing = configRepo.findByTenantIdAndCommitmentId(tenantId, commitmentId).orElse(null);
        if (existing != null) {
            existing.setConfigJson(configJson);
            CommitmentConfigRecord save = configRepo.save(existing);
            auditTrailService.log(
                    AuditEventType.CONFIG_APPLIED,
                    null,
                    commitmentId,
                    "api",
                    Map.of("configHash", Integer.toHexString(configJson.hashCode()))
            );
            return save;
        }

        CommitmentConfigRecord save = configRepo.save(CommitmentConfigRecord.builder()
                .commitmentId(commitmentId)
                .configJson(configJson)
                .tenantId(TenantContext.getTenantId())
                .createdAt(Instant.now())
                .build());

        auditTrailService.log(
                AuditEventType.CONFIG_APPLIED,
                null,
                commitmentId,
                "api",
                Map.of("configHash", Integer.toHexString(configJson.hashCode()))
        );

        return save;
    }

}
