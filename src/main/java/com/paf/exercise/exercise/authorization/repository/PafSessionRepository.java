package com.paf.exercise.exercise.authorization.repository;

import com.paf.exercise.exercise.authorization.domain.PafSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PafSessionRepository  extends JpaRepository<PafSession, UUID> {

    List<PafSession> findByUserId(String userId);

    Optional<PafSession> findBySessionId(String sessionId);
}
