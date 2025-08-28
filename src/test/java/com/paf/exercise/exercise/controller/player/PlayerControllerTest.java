package com.paf.exercise.exercise.controller.player;


import com.paf.exercise.exercise.authorization.service.AuthorizationFilter;
import com.paf.exercise.exercise.authorization.service.SecurityUtilsService;
import com.paf.exercise.exercise.player.service.PlayerService;
import com.paf.exercise.exercise.web.controller.player.PlayerController;
import com.paf.exercise.exercise.web.exception.GlobalException;

import com.paf.exercise.exercise.web.exception.error.PlayerNotFoundError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;



import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerTest {

    @Autowired
    private PlayerController controller;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerService playerService;

    @MockitoBean
    private AuthorizationFilter authorizationFilter;

    @MockitoBean
    private  SecurityUtilsService securityUtils;



    void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }


    public void getPlayers_401() throws Exception {
        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }).when(authorizationFilter)
                .doFilter(any(HttpServletRequest.class),
                        any(HttpServletResponse.class),
                        any(FilterChain.class));

        this.mockMvc.perform(get("/api/v1.0/players")).andDo(print()).andExpect(status().isUnauthorized());
    }



    public void getPlayers_404() throws Exception {
        when(playerService.getPlayers()).thenThrow(new GlobalException(new PlayerNotFoundError()));

        this.mockMvc.perform(get("/api/v1.0/players")).andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PLAYER_NOT_FOUND"));
    }
}
