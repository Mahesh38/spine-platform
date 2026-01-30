package com.spin.core.repo;

import com.spin.core.domain.CommitmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommitmentRepo extends JpaRepository<CommitmentRecord, String> {

    Optional<CommitmentRecord> findByTenantIdAndId(String tenantId, String id);
}
