package com.paf.exercise.exercise.authorization.aop;


import com.paf.exercise.exercise.account.domain.Account;
import com.paf.exercise.exercise.authorization.annotation.RequiredAuthorities;
import com.paf.exercise.exercise.authorization.domain.Authority;
import com.paf.exercise.exercise.authorization.domain.AuthorityCode;
import com.paf.exercise.exercise.authorization.service.SecurityUtilsService;
import com.paf.exercise.exercise.web.exception.GlobalException;
import com.paf.exercise.exercise.web.exception.error.AccountNotFoundError;
import com.paf.exercise.exercise.web.exception.error.InsufficientAuthoritiesError;
import com.paf.exercise.exercise.web.exception.error.UserIsNotAdmin;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Service
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final SecurityUtilsService securityUtils;


    @Around("@annotation(com.paf.exercise.exercise.authorization.annotation.RequiredAuthorities)")
    public Object requiredAuthorities(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Account account = securityUtils.extractAccountFromAuthContext().orElseThrow(()-> new GlobalException(new AccountNotFoundError()));
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();

        RequiredAuthorities annotation = method.getAnnotation(RequiredAuthorities.class);

        /*
            We compare the list of authorities passed in the annotation to the authorities of the account.
            If the account does not include all of them, then we throw a InsufficientAuthoritiesError
         */
        List<AuthorityCode> requiredAuthorities = List.of(annotation.authorities());

        if (!account.getAuthorities().stream().map(Authority::getAuthorityCode).collect(Collectors.toSet()).containsAll(requiredAuthorities)) {
            throw new GlobalException(new InsufficientAuthoritiesError());
        }

        return proceedingJoinPoint.proceed();
    }

    @Before("@annotation(com.paf.exercise.exercise.authorization.annotation.IsAdmin)")
    public void isAdmin() {

        Boolean isAdmin = securityUtils.isAdmin();

        if(!isAdmin){
            throw new GlobalException(new UserIsNotAdmin());
        }

    }
}
