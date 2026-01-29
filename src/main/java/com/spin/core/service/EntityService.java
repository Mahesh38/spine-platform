package com.spin.core.service;

import com.spin.core.domain.EntityRecord;
import com.spin.core.repo.EntityRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EntityService {

  private final EntityRepo entityRepo;

  public EntityRecord create(String type, String name) {
    return entityRepo.save(EntityRecord.builder()
        .type(type)
        .name(name)
        .createdAt(Instant.now())
        .build());
  }
}
