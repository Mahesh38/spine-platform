package com.spin.core.repo;

import com.spin.core.domain.EntityRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepo extends JpaRepository<EntityRecord, String> {

}
