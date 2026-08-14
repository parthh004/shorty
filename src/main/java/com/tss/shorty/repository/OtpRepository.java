package com.tss.shorty.repository;

import com.tss.shorty.entity.Otp;
import com.tss.shorty.entity.User;
import com.tss.shorty.entity.enums.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID>
{
    Optional<Otp> findTopByUserAndTypeOrderByCreatedOnDesc(@Param("user") User user, @Param("type") OtpType type);

    @Query("SELECT COUNT(o) FROM Otp o WHERE o.user = :user AND o.type = :type AND o.createdOn > :date")
    int countByUserAndTypeAndCreatedOnAfter(@Param("user") User user, @Param("type") OtpType type, @Param("date") LocalDateTime date);
}