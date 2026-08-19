package com.tss.shorty.payload.request;

import com.tss.shorty.entity.enums.AuditAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AuditLogExportRequestDto
{
    @NotNull(message = "Action is required")
    @NotBlank(message = "Action is required")
    private AuditAction action;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date can only be on today or in the past date")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @PastOrPresent(message = "End date can only be on today or in the past date")
    private LocalDate endDate;
}