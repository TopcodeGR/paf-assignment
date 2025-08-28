package com.paf.exercise.exercise.authorization.service;

import com.paf.exercise.exercise.authorization.domain.KeycloakTokenIntrospectionResponse;
import com.paf.exercise.exercise.authorization.domain.PafAuthenticationConfigurationProperties;
import com.paf.exercise.exercise.authorization.domain.PafSession;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private final PafAuthenticationConfigurationProperties pafAuthenticationConfigurationProperties;
    private final AuthorizationService authorizationService;
    private final PafSessionService sessionService;


    // If a path SHOULD BE SECURED, this must return false
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().split("/")[1];

        for (String securedPath: pafAuthenticationConfigurationProperties.getSecuredPaths()) {
            if (path.contains(securedPath)) {
                return false;
            }
        }

        return true;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // If the PafSession cookie is not present, the request is considered unauthorized
        if (ObjectUtils.isEmpty(request.getCookies()) ||
                request.getCookies().length == 0){
            authorizationService.handleUnauthenticatedResponse(response);
            return;
        }

        Optional<Cookie> sessionCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(pafAuthenticationConfigurationProperties.getPafSessionCookieName()))
                .findFirst();

        // Same. If the PafSession cookie is not present, the request is considered unauthorized
        if (sessionCookie.isEmpty()) {
            authorizationService.handleUnauthenticatedResponse(response);
            return;
        }

        Optional<PafSession> session = sessionService.getSessionBySessionId(sessionCookie.get().getValue());

        // If the PafSession cookie is present, but the session does not exist in the db,
        // then the request is considered unauthorized
        if (session.isEmpty()) {
            authorizationService.handleUnauthenticatedResponse(response);
            return;
        }

        KeycloakTokenIntrospectionResponse keycloakTokenIntrospectionResponse =
                authorizationService.introspectToken(session.get());

        // If the accessToken is not active, we try to retrieve a new one using the refreshToken.
        // If we succeed, then we consider the request authorized. If we fail to renew the accessToken,
        // then the request in considered unauthorized.
        if (!keycloakTokenIntrospectionResponse.getActive()) {
            try{
                PafSession newSession = authorizationService.authenticateWithRefreshToken(session.get().getRefreshToken(), session.get().getIsAdmin());
                authorizationService.handleAuthenticatedResponse(response, newSession);
                AddUserIdHeaderWrapper mutatedRequest = generateMutatedRequestWithAuthHeaders(request, newSession);
                chain.doFilter(mutatedRequest, response);
            } catch (ResponseStatusException | IOException | ServletException ex) {
                authorizationService.logoutLocal(session.get());
                authorizationService.logoutKeycloak(session.get());
                authorizationService.handleUnauthenticatedResponse(response);
            }
        } else {
            AddUserIdHeaderWrapper mutatedRequest = generateMutatedRequestWithAuthHeaders(request, session.get());
            chain.doFilter(mutatedRequest, response);
        }
    }

    // When a request is authorized, we must append the accessToken, the user id and whether the user is an admin to the
    // request's headers, so we can have access to this information later and be able to enforce further authentication rules.
    private AddUserIdHeaderWrapper generateMutatedRequestWithAuthHeaders(HttpServletRequest request, PafSession session) {
        AddUserIdHeaderWrapper mutatedRequest = new AddUserIdHeaderWrapper(request, pafAuthenticationConfigurationProperties);
        mutatedRequest.putHeader(pafAuthenticationConfigurationProperties.getUserIdHeaderName(), session.getUserId());
        mutatedRequest.putHeader(pafAuthenticationConfigurationProperties.getIsAdminHeaderName(), session.getIsAdmin().toString());
        mutatedRequest.putHeader("Authorization", "Bearer " + session.getAccessToken());
        return mutatedRequest;
    }


    private static class AddUserIdHeaderWrapper extends HttpServletRequestWrapper {

        private final PafAuthenticationConfigurationProperties pafAuthenticationConfigurationProperties;
        private final Map<String, String> customHeaders;

        public AddUserIdHeaderWrapper(HttpServletRequest request, PafAuthenticationConfigurationProperties pafAuthenticationConfigurationProperties) {
            super(request);
            this.customHeaders = new HashMap<>();
            this.pafAuthenticationConfigurationProperties = pafAuthenticationConfigurationProperties;
        }

        public void putHeader(String name, String value){
            this.customHeaders.put(name, value);
        }

        public String getHeader(String name) {

            String headerValue = customHeaders.get(name);

            if (headerValue != null){
                return headerValue;
            }

            return ((HttpServletRequest) getRequest()).getHeader(name);
        }


        public Enumeration<String> getHeaderNames() {

            Set<String> set = new HashSet<>(customHeaders.keySet());

            Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
            while (e.hasMoreElements()) {
                String n = e.nextElement();
                set.add(n);
            }

            return Collections.enumeration(set);
        }


        public Enumeration<String> getHeaders(String name) {
            if (pafAuthenticationConfigurationProperties.getUserIdHeaderName().equalsIgnoreCase(name)) {
                return Collections.enumeration(Collections.singleton(customHeaders.get(pafAuthenticationConfigurationProperties.getUserIdHeaderName())));
            }
            if ("Authorization".equalsIgnoreCase(name)) {
                return Collections.enumeration(Collections.singleton(customHeaders.get("Authorization")));
            }
            if (pafAuthenticationConfigurationProperties.getIsAdminHeaderName().equalsIgnoreCase(name)) {
                return Collections.enumeration(Collections.singleton(customHeaders.get(pafAuthenticationConfigurationProperties.getIsAdminHeaderName())));
            }
            return super.getHeaders(name);
        }
    }
}
