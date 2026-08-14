package com.tss.shorty.entity;

import com.tss.shorty.entity.enums.AuditAction;
import com.tss.shorty.entity.enums.Outcome;
import com.tss.shorty.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuditLogEvent
{
    private Role actor;
    private AuditAction action;
    private String targetEntity;
    private String targetId;
    private Outcome outcome;
}