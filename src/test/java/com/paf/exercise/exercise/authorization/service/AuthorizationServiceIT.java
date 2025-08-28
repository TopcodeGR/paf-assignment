package com.paf.exercise.exercise.authorization.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paf.exercise.exercise.authorization.domain.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(AuthorizationService.class)
public class AuthorizationServiceIT {

    @Autowired
    private AuthorizationService authorizationService;

    @MockitoBean
    private PafAuthenticationConfigurationProperties pafAuthenticationConfigurationProperties;

    @MockitoBean
    private  PafSessionService sessionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private PafSessionService pafSessionService;


    @Test
    public void login_keycloakTokenResponseIsNull_throwUnauthorizedException() throws JsonProcessingException {

        LoginData loginData = LoginData.builder().username("username").password("password").build();

        MultiValueMap<String, String> formDataThatShouldBeUsed = new LinkedMultiValueMap<>();

        formDataThatShouldBeUsed.add("grant_type", "password");
        formDataThatShouldBeUsed.add("username",  "username");
        formDataThatShouldBeUsed.add("password", "password");
        formDataThatShouldBeUsed.add("client_id", "clientId");
        formDataThatShouldBeUsed.add("client_secret", "clientSecret");
        formDataThatShouldBeUsed.add("scope", "openid");

        when(pafAuthenticationConfigurationProperties.getClientId()).thenReturn("clientId");
        when(pafAuthenticationConfigurationProperties.getClientSecret()).thenReturn("clientSecret");
        when(pafAuthenticationConfigurationProperties.getIssuerUri()).thenReturn("http://issuerUri.com");
        when(pafAuthenticationConfigurationProperties.getTokenIssuanceUri()).thenReturn("/tokenIssuanceUri");

        String keycloakTokenResponseString =
                objectMapper.writeValueAsString(null);

        this.server.expect(requestTo("http://issuerUri.com/tokenIssuanceUri"))
                .andRespond(withSuccess(keycloakTokenResponseString, MediaType.APPLICATION_JSON));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authorizationService.login(loginData));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(pafSessionService);
    }

    @Test
    public void login_rolesContainedInIdTokenAreNull_throwUnauthorizedException() throws JsonProcessingException {

        LoginData loginData = LoginData.builder().username("username").password("password").build();

        MultiValueMap<String, String> formDataThatShouldBeUsed = new LinkedMultiValueMap<>();

        formDataThatShouldBeUsed.add("grant_type", "password");
        formDataThatShouldBeUsed.add("username",  "username");
        formDataThatShouldBeUsed.add("password", "password");
        formDataThatShouldBeUsed.add("client_id", "clientId");
        formDataThatShouldBeUsed.add("client_secret", "clientSecret");
        formDataThatShouldBeUsed.add("scope", "openid");

        when(pafAuthenticationConfigurationProperties.getClientId()).thenReturn("clientId");
        when(pafAuthenticationConfigurationProperties.getClientSecret()).thenReturn("clientSecret");
        when(pafAuthenticationConfigurationProperties.getIssuerUri()).thenReturn("http://issuerUri.com");
        when(pafAuthenticationConfigurationProperties.getTokenIssuanceUri()).thenReturn("/tokenIssuanceUri");

        String keycloakTokenResponseString =
                objectMapper.writeValueAsString(KeycloakTokenResponse.builder().idToken(getIdToken(true)).build());

        this.server.expect(requestTo("http://issuerUri.com/tokenIssuanceUri"))
                .andRespond(withSuccess(keycloakTokenResponseString, MediaType.APPLICATION_JSON));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authorizationService.login(loginData));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(pafSessionService);
    }

    @Test
    public void login_adminLoginWhileUserIsNotAdmin_throwUnauthorizedException() throws JsonProcessingException {

        LoginData loginData = LoginData.builder().username("username").password("password").adminLogin(true).build();

        MultiValueMap<String, String> formDataThatShouldBeUsed = new LinkedMultiValueMap<>();

        formDataThatShouldBeUsed.add("grant_type", "password");
        formDataThatShouldBeUsed.add("username",  "username");
        formDataThatShouldBeUsed.add("password", "password");
        formDataThatShouldBeUsed.add("client_id", "clientId");
        formDataThatShouldBeUsed.add("client_secret", "clientSecret");
        formDataThatShouldBeUsed.add("scope", "openid");

        when(pafAuthenticationConfigurationProperties.getClientId()).thenReturn("clientId");
        when(pafAuthenticationConfigurationProperties.getClientSecret()).thenReturn("clientSecret");
        when(pafAuthenticationConfigurationProperties.getIssuerUri()).thenReturn("http://issuerUri.com");
        when(pafAuthenticationConfigurationProperties.getTokenIssuanceUri()).thenReturn("/tokenIssuanceUri");

        String keycloakTokenResponseString =
                objectMapper.writeValueAsString(KeycloakTokenResponse.builder().idToken(getIdToken(false)).build());

        this.server.expect(requestTo("http://issuerUri.com/tokenIssuanceUri"))
                .andRespond(withSuccess(keycloakTokenResponseString, MediaType.APPLICATION_JSON));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authorizationService.login(loginData));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(pafSessionService);
    }

    @Test
    public void login_multipleSessionsExistForTheSameUser_createSessionButDeleteExistingOnesFirst() throws JsonProcessingException {

        LoginData loginData = LoginData.builder().username("username").password("password").adminLogin(false).build();

        MultiValueMap<String, String> formDataThatShouldBeUsed = new LinkedMultiValueMap<>();

        formDataThatShouldBeUsed.add("grant_type", "password");
        formDataThatShouldBeUsed.add("username",  "username");
        formDataThatShouldBeUsed.add("password", "password");
        formDataThatShouldBeUsed.add("client_id", "clientId");
        formDataThatShouldBeUsed.add("client_secret", "clientSecret");
        formDataThatShouldBeUsed.add("scope", "openid");


        PafSession existingSession1 = PafSession
                .builder()
                .sessionId("ses1")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")
                .build();

        PafSession existingSession2 = PafSession
                .builder()
                .sessionId("ses2")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")
                .build();

        PafSession expectedNewSession = PafSession
                .builder()
                .sessionId("ses3")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")
                .build();


        when(pafAuthenticationConfigurationProperties.getClientId()).thenReturn("clientId");
        when(pafAuthenticationConfigurationProperties.getClientSecret()).thenReturn("clientSecret");
        when(pafAuthenticationConfigurationProperties.getIssuerUri()).thenReturn("http://issuerUri.com");
        when(pafAuthenticationConfigurationProperties.getTokenIssuanceUri()).thenReturn("/tokenIssuanceUri");
        when(pafAuthenticationConfigurationProperties.getLogoutUri()).thenReturn("/logoutUri");

        when(sessionService.getSessionsByUserId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")).thenReturn(List.of(existingSession1, existingSession2));
        when(sessionService.createSession(PafSession
                .builder()
                .sessionId(any())
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .idToken(getIdToken(false))
                .userId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")
                .keycloakSession("keycloakSession")
                .isAdmin(false)
                .build())).thenReturn(expectedNewSession);

        String keycloakTokenResponseString =
                objectMapper.writeValueAsString(KeycloakTokenResponse
                        .builder()
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .keycloakSession("keycloakSession")
                        .idToken(getIdToken(false))
                        .build());

        this.server.expect(requestTo("http://issuerUri.com/tokenIssuanceUri"))
                .andRespond(withSuccess(keycloakTokenResponseString, MediaType.APPLICATION_JSON));

        this.server.expect(ExpectedCount.times(2), requestTo("http://issuerUri.com/logoutUri")).andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        PafSession actualNewSession = authorizationService.login(loginData);

        assertThat(actualNewSession).isNotNull();
        assertThat(actualNewSession.getSessionId()).isEqualTo(expectedNewSession.getSessionId());
        assertThat(actualNewSession.getUserId()).isEqualTo(expectedNewSession.getUserId());
        assertThat(actualNewSession.getAccessToken()).isEqualTo(expectedNewSession.getAccessToken());
        assertThat(actualNewSession.getRefreshToken()).isEqualTo(expectedNewSession.getRefreshToken());
        assertThat(actualNewSession.getIdToken()).isEqualTo(expectedNewSession.getIdToken());
        assertThat(actualNewSession.getIsAdmin()).isEqualTo(expectedNewSession.getIsAdmin());
        assertThat(actualNewSession.getKeycloakSession()).isEqualTo(expectedNewSession.getKeycloakSession());

        verify(pafSessionService, times(2)).deleteSession(any(PafSession.class));
    }

    @Test
    public void login() throws JsonProcessingException {

        LoginData loginData = LoginData.builder().username("username").password("password").adminLogin(false).build();

        MultiValueMap<String, String> formDataThatShouldBeUsed = new LinkedMultiValueMap<>();

        formDataThatShouldBeUsed.add("grant_type", "password");
        formDataThatShouldBeUsed.add("username",  "username");
        formDataThatShouldBeUsed.add("password", "password");
        formDataThatShouldBeUsed.add("client_id", "clientId");
        formDataThatShouldBeUsed.add("client_secret", "clientSecret");
        formDataThatShouldBeUsed.add("scope", "openid");

        PafSession expectedNewSession = PafSession
                .builder()
                .sessionId("ses3")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")
                .build();


        when(pafAuthenticationConfigurationProperties.getClientId()).thenReturn("clientId");
        when(pafAuthenticationConfigurationProperties.getClientSecret()).thenReturn("clientSecret");
        when(pafAuthenticationConfigurationProperties.getIssuerUri()).thenReturn("http://issuerUri.com");
        when(pafAuthenticationConfigurationProperties.getTokenIssuanceUri()).thenReturn("/tokenIssuanceUri");
        when(pafAuthenticationConfigurationProperties.getLogoutUri()).thenReturn("/logoutUri");

        when(sessionService.getSessionsByUserId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")).thenReturn(List.of());
        when(sessionService.createSession(PafSession
                .builder()
                .sessionId(any())
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .idToken(getIdToken(false))
                .userId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")
                .keycloakSession("keycloakSession")
                .isAdmin(false)
                .build())).thenReturn(expectedNewSession);

        String keycloakTokenResponseString =
                objectMapper.writeValueAsString(KeycloakTokenResponse
                        .builder()
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .keycloakSession("keycloakSession")
                        .idToken(getIdToken(false))
                        .build());

        this.server.expect(requestTo("http://issuerUri.com/tokenIssuanceUri"))
                .andRespond(withSuccess(keycloakTokenResponseString, MediaType.APPLICATION_JSON));

        this.server.expect(ExpectedCount.times(2), requestTo("http://issuerUri.com/logoutUri")).andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        PafSession actualNewSession = authorizationService.login(loginData);

        assertThat(actualNewSession).isNotNull();
        assertThat(actualNewSession.getSessionId()).isEqualTo(expectedNewSession.getSessionId());
        assertThat(actualNewSession.getUserId()).isEqualTo(expectedNewSession.getUserId());
        assertThat(actualNewSession.getAccessToken()).isEqualTo(expectedNewSession.getAccessToken());
        assertThat(actualNewSession.getRefreshToken()).isEqualTo(expectedNewSession.getRefreshToken());
        assertThat(actualNewSession.getIdToken()).isEqualTo(expectedNewSession.getIdToken());
        assertThat(actualNewSession.getIsAdmin()).isEqualTo(expectedNewSession.getIsAdmin());
        assertThat(actualNewSession.getKeycloakSession()).isEqualTo(expectedNewSession.getKeycloakSession());

        verify(pafSessionService, times(0)).deleteSession(any(PafSession.class));
    }

    @Test
    public void logoutLocal() {
        PafSession session = PafSession
                .builder()
                .sessionId("ses3")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")
                .build();

        authorizationService.logoutLocal(session);

        verify(pafSessionService, times(1)).deleteSession(session);
    }

    @Test
    public void handleAuthenticatedResponse() {

        HttpServletResponse response = new MockHttpServletResponse();

        PafSession session = PafSession
                .builder()
                .sessionId("ses3")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")
                .build();

        when(pafAuthenticationConfigurationProperties.getPafSessionCookieName()).thenReturn("COOKIENAME");

        Cookie cookie = new Cookie("COOKIENAME", "ses3");

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setAttribute("SameSite", "Strict");

        authorizationService.handleAuthenticatedResponse(response, session);

        assertThat(response.getHeader("Set-Cookie")).isNotNull();
        assertThat(Arrays.stream(response.getHeader("Set-Cookie").split(";")).map(String::trim)).contains("COOKIENAME=ses3", "Max-Age=3600", "Path=/", "Secure", "HttpOnly");

    }

    @Test
    public void handleUnauthenticatedResponse() {

        HttpServletResponse response = new MockHttpServletResponse();

        when(pafAuthenticationConfigurationProperties.getPafSessionCookieName()).thenReturn("COOKIENAME");

        Cookie cookie = new Cookie("COOKIENAME", null);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");

        authorizationService.handleUnauthenticatedResponse(response);

        assertThat(response.getHeader("Set-Cookie")).isNotNull();
        assertThat(Arrays.stream(response.getHeader("Set-Cookie").split(";")).map(String::trim)).contains("COOKIENAME=", "Max-Age=0", "Path=/", "Secure", "HttpOnly");
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    public void introspectToken_introspectionFails_logoutAndThrowUnauthorizedException() throws JsonProcessingException {

        PafSession session = PafSession
                .builder()
                .sessionId("ses3")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")
                .build();

        MultiValueMap<String, String> formDataThatShouldBeUsed = new LinkedMultiValueMap<>();

        formDataThatShouldBeUsed.add("token", "at");
        formDataThatShouldBeUsed.add("client_id", "clientId");
        formDataThatShouldBeUsed.add("client_secret", "clientSecret");

        when(pafAuthenticationConfigurationProperties.getClientId()).thenReturn("clientId");
        when(pafAuthenticationConfigurationProperties.getClientSecret()).thenReturn("clientSecret");
        when(pafAuthenticationConfigurationProperties.getIssuerUri()).thenReturn("http://issuerUri.com");
        when(pafAuthenticationConfigurationProperties.getIntrospectUri()).thenReturn("/introspectionUri");
        when(pafAuthenticationConfigurationProperties.getLogoutUri()).thenReturn("/logoutUri");

        this.server.expect(requestTo("http://issuerUri.com/introspectionUri"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        this.server.expect(ExpectedCount.times(1), requestTo("http://issuerUri.com/logoutUri")).andRespond(withSuccess("", MediaType.APPLICATION_JSON));


        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authorizationService.introspectToken(session));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(pafSessionService, times(1)).deleteSession(session);
    }

    @Test
    public void introspectToken() throws JsonProcessingException {

        PafSession session = PafSession
                .builder()
                .sessionId("ses3")
                .accessToken("at")
                .refreshToken("rt")
                .isAdmin(true)
                .idToken("id")
                .keycloakSession("kcs")
                .userId("1cb5e723-df3a-4160-a35d-cc2b5141fe4f")
                .build();

        MultiValueMap<String, String> formDataThatShouldBeUsed = new LinkedMultiValueMap<>();

        formDataThatShouldBeUsed.add("token", "at");
        formDataThatShouldBeUsed.add("client_id", "clientId");
        formDataThatShouldBeUsed.add("client_secret", "clientSecret");

        when(pafAuthenticationConfigurationProperties.getClientId()).thenReturn("clientId");
        when(pafAuthenticationConfigurationProperties.getClientSecret()).thenReturn("clientSecret");
        when(pafAuthenticationConfigurationProperties.getIssuerUri()).thenReturn("http://issuerUri.com");
        when(pafAuthenticationConfigurationProperties.getIntrospectUri()).thenReturn("/introspectionUri");
        when(pafAuthenticationConfigurationProperties.getLogoutUri()).thenReturn("/logoutUri");

        KeycloakTokenIntrospectionResponse expectedKeycloakResponse = KeycloakTokenIntrospectionResponse.builder().active(true).build();
        String keycloakResponseString =
                objectMapper.writeValueAsString(expectedKeycloakResponse);

        this.server.expect(requestTo("http://issuerUri.com/introspectionUri"))
                .andRespond(withSuccess(keycloakResponseString, MediaType.APPLICATION_JSON));

        this.server.expect(ExpectedCount.times(1), requestTo("http://issuerUri.com/logoutUri")).andRespond(withSuccess("", MediaType.APPLICATION_JSON));


        KeycloakTokenIntrospectionResponse actualKeycloakResponse = authorizationService.introspectToken(session);

        assertThat(actualKeycloakResponse.getActive()).isTrue();

        verifyNoInteractions(pafSessionService);
    }

    @Test
    public void decodeJwtToken () {
        String decodedToken = authorizationService.decodeJwtToken(getIdToken(false));

        assertThat(decodedToken).isEqualTo(getIdTokenParsed(false));
    }
    
    private String getIdToken(boolean rolesShouldBeNull) {

        if (!rolesShouldBeNull) {
            return "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJZUHN3enU5UnR3cGhJLVBpZ3Q1Y1FpZm1pSHZYYzI0SEZGaWl0Y0cxd3pJIn0.eyJleHAiOjE3NTYyOTY0ODYsImlhdCI6MTc1NjI5NjE4NiwianRpIjoiZjczNDBjYTEtNzJhMS00ZGEwLTk2OTEtMzNjMDg1MjJlYzNlIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDkwL3JlYWxtcy9wYWYtZXhlcmNpc2UiLCJhdWQiOiJwYWYtZXhlcmNpc2UtY2xpZW50Iiwic3ViIjoiMWNiNWU3MjMtZGYzYS00MTYwLWEzNWQtY2MyYjUxNDFmZTRmIiwidHlwIjoiSUQiLCJhenAiOiJwYWYtZXhlcmNpc2UtY2xpZW50Iiwic2lkIjoiZDQxNDM0YjctM2M4Yi00YzRhLTljYTItMGY2MzM4ODdjOTliIiwiYXRfaGFzaCI6Ik1DT1NLckhfdkhGaFFiU2N2V09EZmciLCJhY3IiOiIxIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXBhZi1leGVyY2lzZSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sIm5hbWUiOiJ0ZXN0dXNlciB0ZXN0dXNlciIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3R1c2VyIiwiZ2l2ZW5fbmFtZSI6InRlc3R1c2VyIiwiZmFtaWx5X25hbWUiOiJ0ZXN0dXNlciIsImVtYWlsIjoidGVzdHVzZXJAZW1haWwuY29tIiwiZW5hYmxlZCI6dHJ1ZX0.I1gej9KggbXHbT1fRFg4t5YhIHoDYsohiUsHkP4QBnQuZwlruY4qjyNt6aKRtKbwRus3iAT7OrQiyMmPxsMQ8YW8vpn4ymMUyXwH3WzoututUXnCXzvGIInR74UtdGGYPOco4MoQUM5pN66615612XVtkfBfF1aiUuKmAVowwPWd531HfW6icXs6XYQtQg76JJOzp09_ryveKPEeS2FpU-MtNF-9kv8HrL1XquC3HIEdUl7aME_C3cqrNWZA359oGEEf1JhPfhGaLddXET1BwVkXMqcsFzKGdML1QOZeGKCa5XPlX_ffXi7O07XFZifgcJ8LjO4EDFf9ugZmkiHFkQ";
        } else {
            return  "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IllQc3d6dTlSdHdwaEktUGlndDVjUWlmbWlIdlhjMjRIRkZpaXRjRzF3ekkifQ.eyJleHAiOjE3NTYyOTY0ODYsImlhdCI6MTc1NjI5NjE4NiwianRpIjoiZjczNDBjYTEtNzJhMS00ZGEwLTk2OTEtMzNjMDg1MjJlYzNlIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDkwL3JlYWxtcy9wYWYtZXhlcmNpc2UiLCJhdWQiOiJwYWYtZXhlcmNpc2UtY2xpZW50Iiwic3ViIjoiMWNiNWU3MjMtZGYzYS00MTYwLWEzNWQtY2MyYjUxNDFmZTRmIiwidHlwIjoiSUQiLCJhenAiOiJwYWYtZXhlcmNpc2UtY2xpZW50Iiwic2lkIjoiZDQxNDM0YjctM2M4Yi00YzRhLTljYTItMGY2MzM4ODdjOTliIiwiYXRfaGFzaCI6Ik1DT1NLckhfdkhGaFFiU2N2V09EZmciLCJhY3IiOiIxIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6bnVsbH0sIm5hbWUiOiJ0ZXN0dXNlciB0ZXN0dXNlciIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3R1c2VyIiwiZ2l2ZW5fbmFtZSI6InRlc3R1c2VyIiwiZmFtaWx5X25hbWUiOiJ0ZXN0dXNlciIsImVtYWlsIjoidGVzdHVzZXJAZW1haWwuY29tIiwiZW5hYmxlZCI6dHJ1ZX0.I1gej9KggbXHbT1fRFg4t5YhIHoDYsohiUsHkP4QBnQuZwlruY4qjyNt6aKRtKbwRus3iAT7OrQiyMmPxsMQ8YW8vpn4ymMUyXwH3WzoututUXnCXzvGIInR74UtdGGYPOco4MoQUM5pN66615612XVtkfBfF1aiUuKmAVowwPWd531HfW6icXs6XYQtQg76JJOzp09_ryveKPEeS2FpU-MtNF-9kv8HrL1XquC3HIEdUl7aME_C3cqrNWZA359oGEEf1JhPfhGaLddXET1BwVkXMqcsFzKGdML1QOZeGKCa5XPlX_ffXi7O07XFZifgcJ8LjO4EDFf9ugZmkiHFkQ";
        }
        
    }

    private String getIdTokenParsed(boolean rolesShouldBeNull) {

        String roles = "";

        if (!rolesShouldBeNull) {
            roles =   "\"roles\":[" +
                    "\"default-roles-paf-exercise\"," +
                    "\"offline_access\"," +
                    "\"uma_authorization\"" +
                    "]";
        } else {
            roles =   "\"roles\": null";
        }
        


        return  "{" +
                "\"exp\":1756296486," +
                "\"iat\":1756296186," +
                "\"jti\":\"f7340ca1-72a1-4da0-9691-33c08522ec3e\"," +
                "\"iss\":\"http://localhost:8090/realms/paf-exercise\"," +
                "\"aud\":\"paf-exercise-client\"," +
                "\"sub\":\"1cb5e723-df3a-4160-a35d-cc2b5141fe4f\"," +
                "\"typ\":\"ID\"," +
                "\"azp\":\"paf-exercise-client\"," +
                "\"sid\":\"d41434b7-3c8b-4c4a-9ca2-0f633887c99b\"," +
                "\"at_hash\":\"MCOSKrH_vHFhQbScvWODfg\"," +
                "\"acr\":\"1\"," +
                "\"email_verified\":true," +
                "\"realm_access\":{" +
                roles +
                "}," +
                "\"name\":\"testuser testuser\"," +
                "\"preferred_username\":\"testuser\"," +
                "\"given_name\":\"testuser\"," +
                "\"family_name\":\"testuser\"," +
                "\"email\":\"testuser@email.com\"," +
                "\"enabled\":true" +
                "}";

    }
}
