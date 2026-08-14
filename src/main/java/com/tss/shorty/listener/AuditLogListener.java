package com.tss.shorty.listener;

import com.tss.shorty.entity.AuditLog;
import com.tss.shorty.entity.AuditLogEvent;
import com.tss.shorty.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogListener
{
    private final AuditLogRepository auditLogRepository;

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAuditLogEvent(AuditLogEvent event)
    {
        try
        {
            AuditLog auditLog = AuditLog.builder()
                    .actor(event.getActor())
                    .action(event.getAction())
                    .targetEntity(event.getTargetEntity())
                    .targetId(event.getTargetId())
                    .outcome(event.getOutcome())
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log saved asynchronously for action: {}", event.getAction());
        }
        catch (Exception e)
        {
            log.error("Failed to save audit log in background thread: {}", e.getMessage());
        }
    }
}