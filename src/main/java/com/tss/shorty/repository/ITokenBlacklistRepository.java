package com.tss.shorty.repository;

import com.tss.shorty.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ITokenBlacklistRepository extends JpaRepository<TokenBlacklist, String>
{

}
