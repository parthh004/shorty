package com.tss.shorty.repository;

import com.tss.shorty.entity.User;
import com.tss.shorty.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    boolean existsByPhoneNo(String phoneNo);

    Optional<User> findByEmail(String email);

    Page<User> findByRole(Role role, Specification<User> userSpecification, Pageable pageable);

    List<User> findByIsEmailVerifiedTrue();
}
