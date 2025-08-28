package com.paf.exercise.exercise.tournament.repository;

import com.paf.exercise.exercise.IntegrationTest;
import com.paf.exercise.exercise.TestValidatorConfiguration;
import com.paf.exercise.exercise.tournament.domain.Tournament;
import com.paf.exercise.exercise.tournament.domain.TournamentRewardCurrency;
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
public class TournamentRepositoryIT extends IntegrationTest {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Validator validator;

    @Test
    void persist_invalidTournament_shouldReturnViolations() {
        
        Tournament tournament = Tournament
                .builder()
                .id(1L)
                .name("")
                .rewardAmount(null)
                .rewardCurrency(null)
                .build();

        Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(3);
        assertThat(violations.stream().map(violation->violation.getPropertyPath().toString()).collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(List.of("name","rewardAmount","rewardCurrency"));
    }

    @Test
    public void persist_validTournament_shouldNotReturnViolations() {
        Tournament tournament = Tournament
                .builder()
                .id(1L)
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();


        Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);

        assertThat(violations).isEmpty();
    }

    @Test
    public void findByName() {
        Tournament tournament = Tournament
                .builder()
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        entityManager.persist(tournament);
        entityManager.flush();
        entityManager.clear();

        Tournament found = tournamentRepository.findByName("tournament1").orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(tournament.getName());
        assertThat(found.getRewardAmount()).isEqualTo(tournament.getRewardAmount());
        assertThat(found.getRewardCurrency()).isEqualTo(tournament.getRewardCurrency());
    }
}
