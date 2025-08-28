package com.paf.exercise.exercise.authorization.service;

import com.paf.exercise.exercise.account.domain.Account;
import com.paf.exercise.exercise.account.repository.AccountRepository;
import com.paf.exercise.exercise.authorization.domain.PafAuthenticationConfigurationProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityUtilsService {

    private final AccountRepository accountRepository;
    private final PafAuthenticationConfigurationProperties pafAuthenticationConfigurationProperties;

    public Optional<Account> extractAccountFromAuthContext() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();

        String userId = request.getHeader(pafAuthenticationConfigurationProperties.getUserIdHeaderName());

        return accountRepository.findByUserId(userId);
    }

    public Boolean isAdmin() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();

        return Boolean.valueOf(request.getHeader(pafAuthenticationConfigurationProperties.getIsAdminHeaderName()));
    }
}
