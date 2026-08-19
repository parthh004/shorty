package com.tss.shorty.repository;

import com.tss.shorty.entity.Transaction;
import com.tss.shorty.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findAllByUser(User currentUser, Pageable pageable);
}
