package com.paf.exercise.exercise.authorization.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paf.exercise.exercise.authorization.domain.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class AuthorizationService {

    private final PafAuthenticationConfigurationProperties pafAuthenticationConfigurationProperties;
    private final ObjectMapper objectMapper;
    private final PafSessionService sessionService;
    private final RestTemplate restTemplate;

    public AuthorizationService(PafAuthenticationConfigurationProperties pafAuthenticationConfigurationProperties,
                                ObjectMapper objectMapper,
                                PafSessionService sessionService,
                                RestTemplateBuilder restTemplateBuilder) {
        this.pafAuthenticationConfigurationProperties = pafAuthenticationConfigurationProperties;
        this.objectMapper = objectMapper;
        this.sessionService = sessionService;
        this.restTemplate = restTemplateBuilder.build();
    }

    public PafSession login(LoginData loginData) throws JsonProcessingException {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "password");
        formData.add("username",  loginData.getUsername());
        formData.add("password", loginData.getPassword());
        formData.add("client_id", pafAuthenticationConfigurationProperties.getClientId());
        formData.add("client_secret", pafAuthenticationConfigurationProperties.getClientSecret());
        formData.add("scope", "openid");

        return authenticate(formData, true, true,loginData.getAdminLogin());
    }

    public void logoutLocal(PafSession session) {
        this.sessionService.deleteSession(session);
    }

    public void handleAuthenticatedResponse(HttpServletResponse response, PafSession session) {
        Cookie sessionCookie = createAuthenticatedSessionCookie(session);
        response.addCookie(sessionCookie);
    }

    public void handleUnauthenticatedResponse(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.addCookie(createUnauthenticatedSessionCookie());
    }

    public KeycloakTokenIntrospectionResponse introspectToken(PafSession session) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("token", session.getAccessToken());
        formData.add("client_id", pafAuthenticationConfigurationProperties.getClientId());
        formData.add("client_secret", pafAuthenticationConfigurationProperties.getClientSecret());

        String url = pafAuthenticationConfigurationProperties.getIssuerUri() + pafAuthenticationConfigurationProperties.getIntrospectUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<KeycloakTokenIntrospectionResponse> response =
                    restTemplate.postForEntity(url, request, KeycloakTokenIntrospectionResponse.class);

            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                logoutLocal(session);
                logoutKeycloak(session);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
            }

            return response.getBody();
        } catch (RestClientException ex) {
            logoutLocal(session);
            logoutKeycloak(session);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }



    }

    public PafSession authenticateWithRefreshToken(String refreshToken, Boolean isAdmin) throws JsonProcessingException {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token",  refreshToken);
        formData.add("client_id", pafAuthenticationConfigurationProperties.getClientId());
        formData.add("client_secret", pafAuthenticationConfigurationProperties.getClientSecret());

        return authenticate(formData, true, false, isAdmin);
    }

    public String decodeJwtToken(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        return new String(decoder.decode(chunks[1]));
    }

    public void logoutKeycloak(PafSession session) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("refresh_token", session.getRefreshToken());
        formData.add("client_id", pafAuthenticationConfigurationProperties.getClientId());
        formData.add("client_secret", pafAuthenticationConfigurationProperties.getClientSecret());

        String url = pafAuthenticationConfigurationProperties.getIssuerUri() + pafAuthenticationConfigurationProperties.getLogoutUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<Void> response =
                restTemplate.postForEntity(url, request, Void.class);

        if (!response.getStatusCode().equals(HttpStatus.OK) && !response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

    }

    private PafSession authenticate(MultiValueMap<String, String>  formData,
                                    Boolean shouldPerformLocalLogout,
                                    Boolean shouldPerformKeycloakLogout,
                                    Boolean adminLogin) throws JsonProcessingException {


        //First authenticate with keycloak with user credentials, to obtain an accessToken, a refreshToken and an id token.
        String url = pafAuthenticationConfigurationProperties.getIssuerUri() + pafAuthenticationConfigurationProperties.getTokenIssuanceUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<KeycloakTokenResponse> response =
                restTemplate.postForEntity(url, request, KeycloakTokenResponse.class);

        if (!response.getStatusCode().equals(HttpStatus.OK) ) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        KeycloakTokenResponse keycloakTokenResponse = response.getBody();


        //If Keycloak doesn't return the tokens, the authorization fails
        if (keycloakTokenResponse == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        //Parse the id token returned by Keyloak and check if the user has admin role.
        //If it's an admin login and the user is not admin, the authorization fails.
        String idToken = decodeJwtToken(keycloakTokenResponse.getIdToken());
        LinkedHashMap<String, Object> idTokenParsed = objectMapper.readValue(idToken, LinkedHashMap.class);
        String userId = idTokenParsed.get("sub").toString();


        if (idTokenParsed.get("realm_access") == null ||
                ((HashMap<String, ArrayList<String>>) idTokenParsed.get("realm_access")).get("roles") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }


        Boolean isAdmin = ((HashMap<String, ArrayList<String>>) idTokenParsed.get("realm_access"))
                .get("roles")
                .contains(pafAuthenticationConfigurationProperties.getAdminRoleName());

        if (adminLogin && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        //Check if the user that tries to authenticate has an already open session
        List<PafSession> existingSessions  = sessionService.getSessionsByUserId(userId);

        //Hypothetical business rule: Do not allow multiple sessions per user
        if (!existingSessions.isEmpty()) {
            for (PafSession session : existingSessions) {
                if (shouldPerformLocalLogout) {
                    logoutLocal(session);
                }

                if (shouldPerformKeycloakLogout) {
                    logoutKeycloak(session);
                }
            }
        }


        //We create a new session and complete the authentication
        return this.sessionService.createSession(PafSession
                .builder()
                .sessionId(UUID.randomUUID().toString())
                .accessToken(keycloakTokenResponse.getAccessToken())
                .refreshToken(keycloakTokenResponse.getRefreshToken())
                .idToken(idToken)
                .userId(userId)
                .keycloakSession(keycloakTokenResponse.getKeycloakSession())
                .isAdmin(isAdmin)
                .build());
    }

    private Cookie createAuthenticatedSessionCookie(PafSession session) {
        Cookie cookie = new Cookie(pafAuthenticationConfigurationProperties.getPafSessionCookieName(), session.getSessionId());

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setAttribute("SameSite", "Strict");

        return cookie;
    }

    private Cookie createUnauthenticatedSessionCookie() {
        Cookie cookie = new Cookie(pafAuthenticationConfigurationProperties.getPafSessionCookieName(), null);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");

        return cookie;
    }
}
