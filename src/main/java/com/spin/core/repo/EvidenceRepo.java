package com.spin.core.repo;

import com.spin.core.domain.EvidenceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenceRepo extends JpaRepository<EvidenceRecord, String> {
    List<EvidenceRecord> findByTenantIdAndCommitmentId(String tenantId, String commitmentId);
}
