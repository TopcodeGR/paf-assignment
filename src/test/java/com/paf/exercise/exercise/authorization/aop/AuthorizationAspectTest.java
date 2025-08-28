package com.paf.exercise.exercise.authorization.aop;

import com.paf.exercise.exercise.account.domain.Account;
import com.paf.exercise.exercise.authorization.annotation.IsAdmin;
import com.paf.exercise.exercise.authorization.annotation.RequiredAuthorities;
import com.paf.exercise.exercise.authorization.domain.Authority;
import com.paf.exercise.exercise.authorization.domain.AuthorityCode;
import com.paf.exercise.exercise.authorization.service.SecurityUtilsService;
import com.paf.exercise.exercise.web.exception.GlobalException;
import com.paf.exercise.exercise.web.exception.error.AccountNotFoundError;
import com.paf.exercise.exercise.web.exception.error.InsufficientAuthoritiesError;
import com.paf.exercise.exercise.web.exception.error.UserIsNotAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorizationAspectTest {


    @Mock
    private SecurityUtilsService securityUtilsService;

    private TestService testService;


    @BeforeEach
    void setUp() {
        testService = new TestService();

        AuthorizationAspect aspect = new AuthorizationAspect(securityUtilsService);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(testService);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        testService = (TestService) aopProxy.getProxy();
    }

    @Test
    void requiredAuthorities() {
        Account account = new Account();
        Authority getPlayerAuthority= Authority
                .builder()
                .authorityCode(AuthorityCode.GET_PLAYER)
                .build();

        Authority createTournamentAuthority = Authority
                .builder()
                .authorityCode(AuthorityCode.CREATE_TOURNAMENT)
                .build();

        account.setAuthorities(Set.of(getPlayerAuthority, createTournamentAuthority));

        when(securityUtilsService.extractAccountFromAuthContext()).thenReturn(Optional.of(account));

        String result = testService.securedMethod();

        assertThat(result).isEqualTo("OK");
    }

    @Test
    void requiredAuthorities_accountDoesNotExist_throwAccountNotFoundError() {

        when(securityUtilsService.extractAccountFromAuthContext()).thenReturn(Optional.empty());

        GlobalException ex = assertThrows(GlobalException.class, () -> testService.securedMethod());

        assertThat(ex.getError()).isInstanceOf(AccountNotFoundError.class);
    }

    @Test
    void requiredAuthorities_resourceAuthorizedEntityDoesNotHaveTheRequiredAuthorities_throwInsufficientAuthoritiesError() {
        Account account = new Account();

        account.setAuthorities(Set.of());

        when(securityUtilsService.extractAccountFromAuthContext()).thenReturn(Optional.of(account));

        GlobalException ex = assertThrows(GlobalException.class, () -> testService.securedMethod());

        assertThat(ex.getError()).isInstanceOf(InsufficientAuthoritiesError.class);
    }

    @Test
    void isAdmin_isAdmin_proceedWithTheMethod () {
        when(securityUtilsService.isAdmin()).thenReturn(true);

        String result = testService.adminOnlyMethod();

        assertThat(result).isEqualTo("OK");
    }


    @Test
    void isAdmin_isNotAdmin_throwUserIsNotAdmin () {
        when(securityUtilsService.isAdmin()).thenReturn(false);

        GlobalException ex = assertThrows(GlobalException.class, () -> testService.adminOnlyMethod());

        assertThat(ex.getError()).isInstanceOf(UserIsNotAdmin.class);
    }


    static class TestService {

        @RequiredAuthorities(authorities = {AuthorityCode.GET_PLAYER, AuthorityCode.CREATE_TOURNAMENT})
        public String securedMethod() {
            return "OK";
        }


        @IsAdmin
        public String adminOnlyMethod() {
            return "OK";
        }
    }
}
