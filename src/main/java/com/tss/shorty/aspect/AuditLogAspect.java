package com.tss.shorty.aspect;

import com.tss.shorty.annotation.AuditActivity;
import com.tss.shorty.entity.AuditLogEvent;
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

        String targetId = joinPoint.getArgs()[0].toString();

        try
        {
            Object result = joinPoint.proceed();
            publishEvent(currentRole, auditActivity.action(), auditActivity.targetEntity(), targetId, Outcome.SUCCESS);
            return result;
        }
        catch (Exception e)
        {
            publishEvent(currentRole, auditActivity.action(), auditActivity.targetEntity(), targetId, Outcome.FAILURE);
            throw e;
        }
    }
}
