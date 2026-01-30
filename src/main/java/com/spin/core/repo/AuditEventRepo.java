package com.spin.core.repo;

import com.spin.core.domain.AuditEventRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditEventRepo extends JpaRepository<AuditEventRecord, String> {
    List<AuditEventRecord> findTop50ByTenantIdAndCommitmentIdOrderByCreatedAtDesc(String tenantId, String commitmentId);
}
