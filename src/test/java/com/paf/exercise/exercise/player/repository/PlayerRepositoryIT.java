package com.paf.exercise.exercise.player.repository;


import com.paf.exercise.exercise.IntegrationTest;
import com.paf.exercise.exercise.TestValidatorConfiguration;
import com.paf.exercise.exercise.player.domain.Player;
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
public class PlayerRepositoryIT extends IntegrationTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Validator validator;

    @Test
    void persist_invalidPlayer_shouldReturnViolations() {
        Player player = Player
                .builder()
                .name("")
                .build();

        Set<ConstraintViolation<Player>> violations = validator.validate(player);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.stream().map(violation->violation.getPropertyPath().toString()).collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(List.of("name"));
    }

    @Test
    public void persist_validPlayer_shouldNotReturnViolations() {
        Player player = Player
                .builder()
                .name("player")
                .build();

        Set<ConstraintViolation<Player>> violations = validator.validate(player);

        assertThat(violations).isEmpty();
    }

    @Test
    public void findByName() {

        Player player = Player
                .builder()
                .name("player")
                .build();

        entityManager.persist(player);
        entityManager.flush();
        entityManager.clear();

        Player found = playerRepository.findByName("player").orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(player.getName());
    }
}
