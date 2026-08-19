package com.tss.shorty.aspect;

import com.tss.shorty.annotation.AuditActivity;
import com.tss.shorty.entity.AuditLogEvent;
import com.tss.shorty.entity.Transaction;
import com.tss.shorty.entity.User;
import com.tss.shorty.entity.enums.AuditAction;
import com.tss.shorty.entity.enums.Outcome;
import com.tss.shorty.entity.enums.Role;
import com.tss.shorty.service.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final ApplicationEventPublisher eventPublisher;
    private final CurrentUserProvider currentUserProvider;


    private void publishEvent(Role actor, AuditAction action, String entity, String targetId, Outcome outcome) {
        eventPublisher.publishEvent(new AuditLogEvent(actor, action, entity, targetId, outcome));
    }

    @Around("@annotation(auditActivity)")
    public Object logAuditActivity(ProceedingJoinPoint joinPoint, AuditActivity auditActivity) throws Throwable {

        Role currentRole = currentUserProvider.get().getRole();
        String targetId = "UNKNOWN";

        try {
            // 1. RUN THE METHOD FIRST
            Object result = joinPoint.proceed();

            // 2. FIND THE ID SMARTLY
            if (result instanceof Transaction) {
                // If the method returned a transaction, grab the newly created ID!
                targetId = ((Transaction) result).getTransactionId().toString();
            } else if (joinPoint.getArgs().length > 0) {
                // Fallback for your Admin methods: Inspect the first argument
                Object firstArg = joinPoint.getArgs()[0];

                if (firstArg instanceof UUID || firstArg instanceof String) {
                    targetId = firstArg.toString(); // It's a clean ID
                } else if (firstArg instanceof User) {
                    targetId = ((User) firstArg).getUserId().toString(); // Safely extract the User ID
                }
            }

            // 3. PUBLISH SUCCESS
            publishEvent(currentRole, auditActivity.action(), auditActivity.targetEntity(), targetId, Outcome.SUCCESS);
            return result;

        } catch (Exception e) {
            // 4. IF IT FAILS, TRY TO SALVAGE THE USER ID FROM ARGUMENTS
            if (joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof User) {
                targetId = ((User) joinPoint.getArgs()[0]).getUserId().toString();
            }
            publishEvent(currentRole, auditActivity.action(), auditActivity.targetEntity(), targetId, Outcome.FAILURE);
            throw e;
        }
    }
}
