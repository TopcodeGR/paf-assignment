package com.paf.exercise.exercise.account.repository;

import com.paf.exercise.exercise.IntegrationTest;
import com.paf.exercise.exercise.TestValidatorConfiguration;
import com.paf.exercise.exercise.account.domain.Account;
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
public class AccountRepositoryIT  extends IntegrationTest {


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Validator validator;

    @Test
    void persist_invalidAccount_shouldReturnViolations() {
        Account account = Account
                .builder()
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.stream().map(violation->violation.getPropertyPath().toString()).collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(List.of("userId"));
    }

    @Test
    public void persist_validAccount_shouldNotViolations() {
        Account account = Account
                .builder()
                .userId("userid")
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);

        assertThat(violations).isEmpty();
    }
    
    @Test
    public void findByUserId() {

        Account account = Account
                .builder()
                .userId("testid")
                .address("test address")
                .build();

        entityManager.persist(account);
        entityManager.flush();
        entityManager.clear();

        Account found = accountRepository.findByUserId("testid").orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getUserId()).isEqualTo(account.getUserId());
        assertThat(found.getAddress()).isEqualTo(account.getAddress());
    }
}
