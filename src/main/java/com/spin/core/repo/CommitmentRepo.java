package com.spin.core.repo;

import com.spin.core.domain.CommitmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitmentRepo extends JpaRepository<CommitmentRecord, String> {

}
