package com.spin.core.api;

import com.spin.core.domain.AuditEventRecord;
import com.spin.core.repo.AuditEventRepo;
import com.spin.core.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditEventRepo repo;

    @GetMapping("/commitments/{commitmentId}")
    public List<AuditEventRecord> recentForCommitment(@PathVariable String commitmentId) {
        String tenantId = TenantContext.getTenantId();
        return repo.findTop50ByTenantIdAndCommitmentIdOrderByCreatedAtDesc(tenantId, commitmentId);
    }
}
