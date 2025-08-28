package com.paf.exercise.exercise.authorization.service;


import com.paf.exercise.exercise.account.domain.Account;
import com.paf.exercise.exercise.account.repository.AccountRepository;
import com.paf.exercise.exercise.authorization.domain.PafAuthenticationConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityUtilsServiceTest {

    @InjectMocks
    private SecurityUtilsService securityUtilsService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PafAuthenticationConfigurationProperties pafAuthenticationConfigurationProperties;

    @Test
    public void extractAccountFromAuthContext() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("x-user-id", "userid");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Account account = Account.builder().userId("userid").build();

        when(pafAuthenticationConfigurationProperties.getUserIdHeaderName()).thenReturn("x-user-id");
        when(accountRepository.findByUserId("userid")).thenReturn(Optional.of(account));

        Optional<Account> actualAccount = securityUtilsService.extractAccountFromAuthContext();

        assertThat(actualAccount.isPresent()).isTrue();
        assertThat(actualAccount.get()).isEqualTo(account);

        verify(accountRepository, times(1)).findByUserId("userid");
    }

    @Test
    public void isAdmin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("x-is-admin", true);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(pafAuthenticationConfigurationProperties.getIsAdminHeaderName()).thenReturn("x-is-admin");

        Boolean actualValue = securityUtilsService.isAdmin();

        assertThat(actualValue).isTrue();
    }
}
