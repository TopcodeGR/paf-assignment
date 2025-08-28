package com.paf.exercise.exercise.authorization.service;


import com.paf.exercise.exercise.authorization.domain.PafSession;
import com.paf.exercise.exercise.authorization.repository.PafSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PafSessionServiceTest {

    @InjectMocks
    private PafSessionService pafSessionService;

    @Mock
    private PafSessionRepository sessionRepository;

    @Test
    public void createSession() {
        PafSession expectedSession = PafSession
                .builder()
                .sessionId("ses1")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("userid")
                .build();

        when(sessionRepository.save(expectedSession)).thenReturn(expectedSession);

        PafSession actualSession = pafSessionService.createSession(expectedSession);

        assertThat(actualSession).isEqualTo(expectedSession);

        verify(sessionRepository, times(1)).save(any());

    }

    @Test
    public void deleteSession() {
        PafSession session = PafSession
                .builder()
                .sessionId("ses1")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("userid")
                .build();

        pafSessionService.deleteSession(session);

        verify(sessionRepository, times(1)).delete(any());
    }

    @Test
    public void getSessionBySessionId() {
        PafSession expectedSession = PafSession
                .builder()
                .sessionId("ses1")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("userid")
                .build();

        when(sessionRepository.findBySessionId("ses1")).thenReturn(Optional.of(expectedSession));

        Optional<PafSession> actualSession = pafSessionService.getSessionBySessionId("ses1");

        assertThat(actualSession.isPresent()).isTrue();
        assertThat(actualSession.get()).isEqualTo(expectedSession);
        verify(sessionRepository, times(1)).findBySessionId("ses1");
    }

    @Test
    public void getSessionsByUserId() {
        PafSession session1 = PafSession
                .builder()
                .sessionId("ses1")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("userid")
                .build();

        PafSession session2 = PafSession
                .builder()
                .sessionId("ses1")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("userid")
                .build();


        when(sessionRepository.findByUserId("userid")).thenReturn(List.of(session1,session2));

        List<PafSession> sessions = pafSessionService.getSessionsByUserId("userid");

        assertThat(sessions).containsExactlyElementsOf(List.of(session1,session2));
        verify(sessionRepository, times(1)).findByUserId("userid");
    }
}
