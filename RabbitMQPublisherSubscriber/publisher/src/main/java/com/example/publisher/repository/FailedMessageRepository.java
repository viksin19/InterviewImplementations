package com.example.publisher.repository;

import com.example.publisher.entity.FailedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedMessageRepository extends JpaRepository<FailedMessage, Long> {
}