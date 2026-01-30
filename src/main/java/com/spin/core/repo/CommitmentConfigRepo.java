package com.spin.core.repo;

import com.spin.core.domain.CommitmentConfigRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommitmentConfigRepo extends JpaRepository<CommitmentConfigRecord, String> {
    Optional<CommitmentConfigRecord> findByTenantIdAndCommitmentId(String tenantId, String commitmentId);
}
