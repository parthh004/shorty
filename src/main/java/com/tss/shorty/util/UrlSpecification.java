package com.tss.shorty.util;

import com.tss.shorty.entity.Url;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class UrlSpecification {
    public static Specification<Url> isActive() {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isActive")));
    }

    public static Specification<Url> accessedFromDateToNow(LocalDate fromDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null) {
                return null;
            }
            LocalDateTime startOfDay = fromDate.atStartOfDay(); //start of given date
            LocalDateTime now = LocalDateTime.now(); //end of today

            return criteriaBuilder.between(root.get("lastAccessedOn"), startOfDay, now);
        };
    }

    public static Specification<Url> expiresOnDate(LocalDate targetDate) {
        return (root, query, criteriaBuilder) -> {
            if (targetDate == null) {
                return null;
            }

            LocalDateTime startOfDay = targetDate.atStartOfDay(); // 00:00:00
            LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX); // 23:59:59.999999

            // Generates: WHERE expiry_date BETWEEN 00:00:00 AND 23:59:59
            return criteriaBuilder.between(root.get("expiryDate"), startOfDay, endOfDay);
        };
    }

    public static Specification<Url> hasCustomAlias(Boolean hasCustomAlias) {
        return (root, query, criteriaBuilder) -> {
            if (hasCustomAlias == null) return null;
            return criteriaBuilder.equal(root.get("customAlias"), hasCustomAlias);
        };
    }

    public static Specification<Url> hasExpired(Boolean hasExpired) {
        return (root, query, criteriaBuilder) -> {
            if (hasExpired == null) return null;
            return criteriaBuilder.equal(root.get("isExpired"), hasExpired);
        };
    }

    public static Specification<Url> belongsToUser(UUID userId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("user").get("id"), userId);
        };
    }

}
