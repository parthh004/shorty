package com.tss.shorty.mapper;

import com.tss.shorty.entity.AuditLog;
import com.tss.shorty.payload.response.AuditLogResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditMapper
{
    AuditLogResponseDto toDto(AuditLog auditLog);
}