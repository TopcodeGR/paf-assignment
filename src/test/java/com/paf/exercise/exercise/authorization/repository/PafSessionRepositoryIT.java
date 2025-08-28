package com.paf.exercise.exercise.authorization.repository;


import com.paf.exercise.exercise.IntegrationTest;
import com.paf.exercise.exercise.TestValidatorConfiguration;
import com.paf.exercise.exercise.authorization.domain.PafSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DataJpaTest
@Testcontainers
@Import(value = {TestValidatorConfiguration.class})
public class PafSessionRepositoryIT  extends IntegrationTest {


    @Autowired
    private PafSessionRepository pafSessionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Validator validator;


    @Test
    void persist_invalidSession_shouldReturnViolations() {
        PafSession session = PafSession
                .builder()
                .sessionId("ses2")
                .userId("userid")
                .build();

        Set<ConstraintViolation<PafSession>> violations = validator.validate(session);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(5);
        assertThat(violations.stream().map(violation->violation.getPropertyPath().toString()).collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(List.of("refreshToken","accessToken","idToken","keycloakSession", "isAdmin"));
    }

    @Test
    public void persist_validSession_shouldNotViolations() {
        PafSession session = PafSession
                .builder()
                .sessionId("ses2")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("userid")
                .build();

        Set<ConstraintViolation<PafSession>> violations = validator.validate(session);

        assertThat(violations).isEmpty();
    }

    @Test
    public void findByUserId() {

        PafSession session1 = PafSession
                .builder()
                .sessionId("ses2")
                .userId("userid")
                .accessToken("at")
                .refreshToken("rt")
                .idToken("id")
                .isAdmin(true)
                .keycloakSession("kcs")
                .build();

        PafSession session2 = PafSession
                .builder()
                .sessionId("ses2")
                .userId("userid")
                .accessToken("at")
                .refreshToken("rt")
                .idToken("id")
                .keycloakSession("kcs")
                .isAdmin(true)
                .build();

        entityManager.persist(session1);
        entityManager.persist(session2);
        entityManager.flush();
        entityManager.clear();

        List<PafSession> found = pafSessionRepository.findByUserId("userid");
        assertThat(found).isNotNull();
        assertThat(found).hasSize(2);
    }

    @Test
    public void findBySessionId() {

        PafSession session = PafSession
                .builder()
                .sessionId("ses1")
                .userId("userid")
                .accessToken("at")
                .refreshToken("rt")
                .idToken("id")
                .isAdmin(true)
                .keycloakSession("kcs")
                .build();


        entityManager.persist(session);
        entityManager.flush();
        entityManager.clear();

        PafSession found = pafSessionRepository.findBySessionId("ses1").orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getSessionId()).isEqualTo(session.getSessionId());
        assertThat(found.getUserId()).isEqualTo(session.getUserId());
        assertThat(found.getAccessToken()).isEqualTo(session.getAccessToken());
        assertThat(found.getRefreshToken()).isEqualTo(session.getRefreshToken());
        assertThat(found.getIdToken()).isEqualTo(session.getIdToken());
        assertThat(found.getIsAdmin()).isEqualTo(session.getIsAdmin());
        assertThat(found.getKeycloakSession()).isEqualTo(session.getKeycloakSession());
    }
}
