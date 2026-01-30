package com.spin.core.service;

import com.spin.core.audit.AuditEventType;
import com.spin.core.domain.CommitmentConfigRecord;
import com.spin.core.domain.CommitmentRecord;
import com.spin.core.domain.CommitmentState;
import com.spin.core.domain.EvidenceRecord;
import com.spin.core.dto.EvaluationFacts;
import com.spin.core.dto.EvaluationResult;
import com.spin.core.engine.JsonUtil;
import com.spin.core.engine.SpineConfig;
import com.spin.core.engine.SpineEvaluator;
import com.spin.core.repo.CommitmentConfigRepo;
import com.spin.core.repo.CommitmentRepo;
import com.spin.core.repo.EvidenceRepo;
import com.spin.core.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationOrchestrator {

    private final CommitmentRepo commitmentRepo;
    private final EvidenceRepo evidenceRepo;
    private final CommitmentConfigRepo configRepo;
    private final AuditTrailService auditTrailService;
    private final FactBuilder factBuilder;
    private final JsonUtil jsonUtil;
    private final SpineEvaluator spineEvaluator;


    public CommitmentRecord evaluateCommitment(String commitmentId) {

        String tenantId = TenantContext.getTenantId();

        CommitmentRecord c = commitmentRepo.findByTenantIdAndId(tenantId, commitmentId).orElseThrow();

        CommitmentConfigRecord cfgRec = configRepo.findByTenantIdAndCommitmentId(tenantId, commitmentId)
                .orElseThrow(() -> new IllegalStateException("No config configured for this commitment."));

        SpineConfig cfg = jsonUtil.toConfig(cfgRec.getConfigJson());

        Map<String, Object> commitmentAttrs = jsonUtil.toMap(c.getAttributesJson());

        List<EvidenceRecord> evidence = evidenceRepo.findByTenantIdAndCommitmentId(tenantId, commitmentId);
        List<Map<String, Object>> evidenceMaps = evidence.stream()
                .map(e -> jsonUtil.toMap(e.getAttributesJson()))
                .toList();

        Instant now = Instant.now();
        EvaluationFacts facts = factBuilder.build(c.getState().name(), commitmentAttrs, evidenceMaps, now);

        auditTrailService.log(
                AuditEventType.EVALUATION_RUN,
                null,
                commitmentId,
                "system",
                facts.getSummary()
        );


        EvaluationResult next = spineEvaluator.evaluateNextState(
                cfg,
                facts.getCurrentState(),
                commitmentAttrs,
                facts.getEvidenceKinds(),
                facts.getEvidenceCount(),
                facts.getNow()
        );
        CommitmentState oldState = c.getState();
        CommitmentState nextState = CommitmentState.valueOf(next.getNextState());

        if (!Objects.equals(oldState, nextState)) {
            c.setState(nextState);
            c.setUpdatedAt(Instant.now());
            c = commitmentRepo.save(c);

            auditTrailService.log(
                    AuditEventType.STATE_CHANGED,
                    null,
                    commitmentId,
                    "system",
                    Map.of(
                            "oldState", oldState,
                            "newState", nextState,
                            "matchedRule", next.getMatchedRuleName(),
                            "matchedRuleIndex", next.getMatchedRuleIndex(),
                            "matchedWhen", next.getMatchedWhen()
                    )
            );
        }
        return c;
    }

}
