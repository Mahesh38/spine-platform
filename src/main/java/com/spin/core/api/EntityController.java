package com.spin.core.api;

import com.spin.core.domain.EntityRecord;
import com.spin.core.service.EntityService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/entities")
@RequiredArgsConstructor
public class EntityController {

    private final EntityService entityService;

    @PostMapping
    public EntityRecord create(@RequestBody CreateEntityReq req) {
        return entityService.create(req.type, req.name);
    }

    @Data
    public static class CreateEntityReq {
        @NotBlank
        public String type;
        @NotBlank
        public String name;
    }
}
