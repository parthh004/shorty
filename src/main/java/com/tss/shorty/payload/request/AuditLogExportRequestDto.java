package com.tss.shorty.payload.request;

import com.tss.shorty.entity.enums.AuditAction;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AuditLogExportRequestDto
{
    private AuditAction action;
    private LocalDate startDate;
    private LocalDate endDate;
}