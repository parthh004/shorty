package com.tss.shorty.util;

import com.tss.shorty.entity.User;
import com.tss.shorty.entity.enums.Role;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification
{
    public static Specification<User> hasRole(Role role) {
        return (root, query, cb) ->
        {
            if (role == null) return null;
            return cb.equal(root.get("role"), role);
        };
    }

    public static Specification<User> nameContains(String name)
    {
        return (root, query, cb) ->
        {
            if (name == null || name.isBlank()) return null;
            return cb.like(cb.lower(root.get("userName")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<User> emailContains(String email)
    {
        return (root, query, cb) ->
        {
            if (email == null || email.isBlank()) return null;
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<User> isActive(Boolean isActive)
    {
        return (root, query, cb) ->
        {
            if (isActive == null) return null;
            return cb.equal(root.get("isActive"), isActive);
        };
    }
}