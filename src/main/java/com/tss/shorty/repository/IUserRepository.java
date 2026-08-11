package com.tss.shorty.repository;

import com.tss.shorty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID>
{
    boolean existsByEmail(String email);

    boolean existsByPhoneNo(String phoneNo);

    Optional<User> findByEmail(String email);
}
