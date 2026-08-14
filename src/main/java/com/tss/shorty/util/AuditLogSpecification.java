package com.tss.shorty.util;

import com.tss.shorty.entity.AuditLog;
import com.tss.shorty.entity.enums.AuditAction;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalTime;

public class AuditLogSpecification
{

    public static Specification<AuditLog> hasAction(AuditAction action)
    {
        return (root, query, cb) ->
        {
            if (action == null) return null;
            return cb.equal(root.get("action"), action);
        };
    }

    public static Specification<AuditLog> fromDate(LocalDate startDate)
    {
        return (root, query, cb) ->
        {
            if (startDate == null) return null;
            return cb.greaterThanOrEqualTo(root.get("createdOn"), startDate.atStartOfDay());
        };
    }

    public static Specification<AuditLog> toDate(LocalDate endDate)
    {
        return (root, query, cb) ->
        {
            if (endDate == null) return null;
            return cb.lessThanOrEqualTo(root.get("createdOn"), endDate.atTime(LocalTime.MAX));
        };
    }
}