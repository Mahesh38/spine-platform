package com.spin.core.service;

import com.spin.core.domain.CommitmentConfigRecord;
import com.spin.core.domain.CommitmentRecord;
import com.spin.core.domain.CommitmentState;
import com.spin.core.domain.EvidenceRecord;
import com.spin.core.engine.JsonUtil;
import com.spin.core.engine.SpineConfig;
import com.spin.core.engine.SpineEvaluator;
import com.spin.core.repo.CommitmentConfigRepo;
import com.spin.core.repo.CommitmentRepo;
import com.spin.core.repo.EvidenceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommitmentService {

  private final CommitmentRepo commitmentRepo;
  private final EvidenceRepo evidenceRepo;
  private final CommitmentConfigRepo configRepo;

  private final JsonUtil jsonUtil;
  private final SpineEvaluator spineEvaluator;

  public CommitmentRecord create(String title, String ownerEntityId, Map<String, Object> attributes) {
    String attrsJson = jsonUtil.toJson(attributes == null ? Map.of() : attributes);

    return commitmentRepo.save(CommitmentRecord.builder()
        .title(title)
        .ownerEntityId(ownerEntityId)
        .state(CommitmentState.CREATED)
        .attributesJson(attrsJson)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build());
  }

  public CommitmentRecord activate(String id) {
    CommitmentRecord c = commitmentRepo.findById(id).orElseThrow();
    c.setState(CommitmentState.ACTIVE);
    c.setUpdatedAt(Instant.now());
    return commitmentRepo.save(c);
  }

  public CommitmentConfigRecord configure(String commitmentId, String configJson) {
    // Validate config now
    jsonUtil.toConfig(configJson);

    CommitmentConfigRecord existing = configRepo.findByCommitmentId(commitmentId).orElse(null);
    if (existing != null) {
      existing.setConfigJson(configJson);
      return configRepo.save(existing);
    }

    return configRepo.save(CommitmentConfigRecord.builder()
        .commitmentId(commitmentId)
        .configJson(configJson)
        .createdAt(Instant.now())
        .build());
  }

  public EvidenceRecord addEvidence(String commitmentId, Map<String, Object> evidenceAttrs) {
    String json = jsonUtil.toJson(evidenceAttrs == null ? Map.of() : evidenceAttrs);

    return evidenceRepo.save(EvidenceRecord.builder()
        .commitmentId(commitmentId)
        .attributesJson(json)
        .createdAt(Instant.now())
        .build());
  }

  public CommitmentRecord evaluate(String commitmentId) {
    CommitmentRecord c = commitmentRepo.findById(commitmentId).orElseThrow();

    CommitmentConfigRecord cfgRec = configRepo.findByCommitmentId(commitmentId)
        .orElseThrow(() -> new IllegalStateException("No config configured for this commitment."));

    SpineConfig cfg = jsonUtil.toConfig(cfgRec.getConfigJson());

    Map<String, Object> commitmentAttrs = jsonUtil.toMap(c.getAttributesJson());

    List<EvidenceRecord> evidence = evidenceRepo.findByCommitmentId(commitmentId);
    List<Map<String, Object>> evidenceMaps = evidence.stream()
        .map(e -> jsonUtil.toMap(e.getAttributesJson()))
        .toList();

    Set<String> evidenceKinds = evidenceMaps.stream()
        .map(m -> String.valueOf(m.getOrDefault("kind", "")))
        .filter(s -> s != null && !s.isBlank())
        .collect(Collectors.toSet());

    int evidenceCount = evidence.size();

    String next = spineEvaluator.evaluateNextState(
        cfg,
        c.getState().name(),
        commitmentAttrs,
        evidenceKinds,
        evidenceCount,
        Instant.now()
    );

    c.setState(CommitmentState.valueOf(next));
    c.setUpdatedAt(Instant.now());
    return commitmentRepo.save(c);
  }

  public CommitmentRecord get(String id) {
    return commitmentRepo.findById(id).orElseThrow();
  }
}
