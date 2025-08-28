package com.paf.exercise.exercise.authorization.service;


import com.paf.exercise.exercise.authorization.domain.PafSession;
import com.paf.exercise.exercise.authorization.repository.PafSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PafSessionService {

    private final PafSessionRepository sessionRepository;


    @Transactional
    public PafSession createSession(PafSession session) {
        return sessionRepository.save(session);
    }


    @Transactional
    public void deleteSession(PafSession session) {
        sessionRepository.delete(session);
    }


    public Optional<PafSession> getSessionBySessionId(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }

    public List<PafSession> getSessionsByUserId(String userId) {
        return sessionRepository.findByUserId(userId);
    }
}
