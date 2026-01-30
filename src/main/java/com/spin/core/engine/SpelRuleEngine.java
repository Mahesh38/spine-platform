package com.spin.core.engine;

import com.spin.core.dto.EvaluationResult;
import lombok.Getter;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Component
public class SpelRuleEngine {

    private final ExpressionParser parser = new SpelExpressionParser();

    public EvaluationResult proposeNextState(SpineConfig cfg,
                                             String currentState,
                                             Map<String, Object> commitmentAttrs,
                                             Set<String> evidenceKinds,
                                             int evidenceCount,
                                             Instant now) {

        Actions A = new Actions();
        Fn F = new Fn();

        StandardEvaluationContext ctx = new StandardEvaluationContext();

        ctx.setVariable("state", currentState);
        ctx.setVariable("now", now);
        ctx.setVariable("commitment", commitmentAttrs);
        ctx.setVariable("evidenceKinds", evidenceKinds);
        ctx.setVariable("evidenceCount", evidenceCount);

        ctx.setVariable("A", A);
        ctx.setVariable("F", F);

        // block type access like T(java.lang.Runtime)
        ctx.setTypeLocator(typeName -> {
            throw new SecurityException("Type access disabled");
        });

        EvaluationResult nullableResult = EvaluationResult.builder().nextState(null).build();
        if (cfg.getRules() == null)
            return nullableResult;
        int i = 0;
        for (SpineConfig.RuleDef rule : cfg.getRules()) {
            Boolean ok = parser.parseExpression(rule.getWhen()).getValue(ctx, Boolean.class);
            if (Boolean.TRUE.equals(ok)) {
                parser.parseExpression(rule.getThen()).getValue(ctx);
                if (A.getNextState() != null) {
                    return EvaluationResult.builder()
                            .nextState(A.getNextState())
                            .matchedRuleName(rule.getName())
                            .matchedRuleIndex(i)
                            .matchedWhen(rule.getWhen())
                            .build();
                }
            }
            i++;
        }
        return nullableResult;
    }

    public static class Fn {
        public Instant parseInstant(Object s) {
            if (s == null) return null;
            try {
                return Instant.parse(String.valueOf(s));
            } catch (RuntimeException e) {
                return null;
            }
        }
    }

    public static class Actions {
        @Getter
        private String nextState;

        public void setState(String s) {
            this.nextState = s;
        }
    }
}
