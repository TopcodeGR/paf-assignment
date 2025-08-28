package com.paf.exercise.exercise.web.controller.autentication;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.paf.exercise.exercise.authorization.domain.LoginData;
import com.paf.exercise.exercise.authorization.domain.PafAuthenticationConfigurationProperties;
import com.paf.exercise.exercise.authorization.domain.PafSession;
import com.paf.exercise.exercise.authorization.service.AuthorizationService;
import com.paf.exercise.exercise.authorization.service.PafSessionService;
import com.paf.exercise.exercise.web.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;


@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authorization")
public class AuthorizationController {

    private final AuthorizationService authorizationService;
    private final PafSessionService sessionService;
    private final PafAuthenticationConfigurationProperties pafAuthenticationConfigurationProperties;


    @PostMapping("/login")
    @Operation(summary = "Login a user")
    @ApiResponse(responseCode="200", description = "OK")
    @ApiResponse(responseCode="401", description = "Unauthorized")
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    public void login(@RequestBody LoginData body, HttpServletResponse response) throws JsonProcessingException {

        try{
            PafSession session = authorizationService.login(body);
            authorizationService.handleAuthenticatedResponse(response, session);
        } catch (ResponseStatusException ex) {
            authorizationService.handleUnauthenticatedResponse(response);

        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout a user")
    @ApiResponse(responseCode="200", description = "OK")
    @ApiResponse(responseCode="401")
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response)  {

        if (ObjectUtils.isEmpty(request.getCookies()) ||
                request.getCookies().length == 0){
            authorizationService.handleUnauthenticatedResponse(response);
            return ResponseEntity.status(response.getStatus()).build();
        }


        Optional<Cookie> sessionCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(pafAuthenticationConfigurationProperties.getPafSessionCookieName()))
                .findFirst();


        if (sessionCookie.isPresent()){
            Optional<PafSession> session = sessionService.getSessionBySessionId(sessionCookie.get().getValue());

            if (session.isPresent()) {
                authorizationService.logoutLocal(session.get());
                authorizationService.logoutKeycloak(session.get());
            }
        }

        authorizationService.handleUnauthenticatedResponse(response);
        return ResponseEntity.status(response.getStatus()).build();
    }
}
