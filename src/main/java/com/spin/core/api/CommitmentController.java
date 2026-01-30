package com.spin.core.api;

import com.spin.core.domain.CommitmentConfigRecord;
import com.spin.core.domain.CommitmentRecord;
import com.spin.core.domain.EvidenceRecord;
import com.spin.core.service.CommitmentConfigService;
import com.spin.core.service.CommitmentService;
import com.spin.core.service.EvidenceService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/commitments")
@RequiredArgsConstructor
public class CommitmentController {

    private final CommitmentService commitmentService;
    private final CommitmentConfigService commitmentConfigService;
    private final EvidenceService evidenceService;

    @PostMapping
    public CommitmentRecord create(@RequestBody CreateCommitmentReq req) {
        return commitmentService.create(req.title, req.ownerEntityId, req.attributes);
    }

    @PostMapping("/{id}/activate")
    public CommitmentRecord activate(@PathVariable String id) {
        return commitmentService.activate(id);
    }

    @PostMapping("/{id}/configure")
    public CommitmentConfigRecord configure(@PathVariable String id, @RequestBody ConfigureReq req) {
        return commitmentConfigService.configure(id, req.configJson);
    }

    @PostMapping("/{id}/evidence")
    public EvidenceRecord addEvidence(@PathVariable String id, @RequestBody AddEvidenceReq req) {
        return evidenceService.addEvidence(id, req.getAttributes());
    }

    @PostMapping("/{id}/evaluate")
    public CommitmentRecord evaluate(@PathVariable String id) {
        return commitmentService.evaluate(id);
    }

    @GetMapping("/{id}")
    public CommitmentRecord get(@PathVariable String id) {
        return commitmentService.get(id);
    }

    @Data
    public static class CreateCommitmentReq {
        @NotBlank
        public String title;
        @NotBlank
        public String ownerEntityId;
        public Map<String, Object> attributes;
    }

    @Data
    public static class ConfigureReq {
        @NotBlank
        public String configJson;
    }

    @Data
    public static class AddEvidenceReq {
        public Map<String, Object> attributes;
    }
}
