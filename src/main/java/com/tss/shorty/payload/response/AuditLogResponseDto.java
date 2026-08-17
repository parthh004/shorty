package com.tss.shorty.payload.response;

import com.tss.shorty.entity.enums.AuditAction;
import com.tss.shorty.entity.enums.Outcome;
import com.tss.shorty.entity.enums.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDto
{
    private UUID id;
    private Role actor;
    private AuditAction action;
    private String targetEntity;
    private String targetId;
    private Outcome outcome;
    private LocalDateTime createdOn;
}