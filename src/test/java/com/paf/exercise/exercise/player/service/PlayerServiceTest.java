package com.paf.exercise.exercise.player.service;


import com.paf.exercise.exercise.player.domain.Player;
import com.paf.exercise.exercise.player.domain.dto.CreatePlayerRequestBodyDTO;
import com.paf.exercise.exercise.player.domain.dto.GetPlayerResponseDTO;
import com.paf.exercise.exercise.player.domain.dto.UpdatePlayerRequestBodyDTO;
import com.paf.exercise.exercise.player.mapper.PlayerMapper;
import com.paf.exercise.exercise.player.repository.PlayerRepository;
import com.paf.exercise.exercise.player.validation.PlayerValidator;
import com.paf.exercise.exercise.web.exception.GlobalException;
import com.paf.exercise.exercise.web.exception.error.PlayerAlreadyExistsError;
import com.paf.exercise.exercise.web.exception.error.PlayerNotFoundError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @InjectMocks
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;

    @Spy
    private PlayerMapper playerMapper;

    @Mock
    private PlayerValidator validator;

    @Mock
    private PlayerValidator playerValidator;


    @Test
    public void getPlayers() {

        Player player1 = Player.builder().id(1L).name("player1").build();
        Player player2 = Player.builder().id(2L).name("player2").build();

        GetPlayerResponseDTO player1DTO = GetPlayerResponseDTO.builder().id(1L).name("player1").build();
        GetPlayerResponseDTO player2DTO = GetPlayerResponseDTO.builder().id(2L).name("player2").build();

        when(playerRepository.findAll()).thenReturn(List.of(player1,player2));
        when(playerMapper.playerEntityToGetTournamentDTO(player1)).thenReturn(player1DTO);
        when(playerMapper.playerEntityToGetTournamentDTO(player2)).thenReturn(player2DTO);

        List<GetPlayerResponseDTO> actualPlayers = playerService.getPlayers();

        assertThat(actualPlayers.size()).isEqualTo(2);
        assertThat(actualPlayers).containsExactlyElementsOf(List.of(player1DTO, player2DTO));

        verify(playerRepository, times(1)).findAll();
        verify(playerMapper, times(2)).playerEntityToGetTournamentDTO(any());
    }

    @Test
    public void getPlayer_playerDoesNotExist_throwException() {

        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException ex = assertThrows(GlobalException.class, () -> playerService.getPlayer(1L));

        assertThat(ex.getError()).isInstanceOf(PlayerNotFoundError.class);

        verify(playerRepository, times(1)).findById(1L);
    }

    @Test
    public void getPlayer() {

        Player player = Player.builder().id(1L).name("player1").build();
        GetPlayerResponseDTO playerDTO = GetPlayerResponseDTO.builder().id(1L).name("player1").build();

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(playerMapper.playerEntityToGetTournamentDTO(player)).thenReturn(playerDTO);

        GetPlayerResponseDTO actualPlayer = playerService.getPlayer(1L);

        assertThat(actualPlayer).isEqualTo(playerDTO);

        verify(playerRepository, times(1)).findById(1L);
        verify(playerMapper, times(1)).playerEntityToGetTournamentDTO(any());
    }

    @Test
    public void getPlayerEntity_playerDoesNotExist_throwException() {

        GlobalException ex = assertThrows(GlobalException.class, () -> playerService.getPlayerEntity(1L));

        assertThat(ex.getError()).isInstanceOf(PlayerNotFoundError.class);

        verify(playerRepository, times(1)).findById(1L);
    }

    @Test
    public void getPlayerEntity() {

        Player player = Player.builder().id(1L).name("player1").build();

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Player actualPlayer = playerService.getPlayerEntity(1L);

        assertThat(actualPlayer).isEqualTo(player);

        verify(playerRepository, times(1)).findById(1L);
    }

    @Test
    public void createPlayer_playerAlreadyExists_throwException() {

        CreatePlayerRequestBodyDTO createPlayerRequestBodyDTO =
                CreatePlayerRequestBodyDTO.builder().name("player1").build();

        Player player = Player.builder().id(1L).name("player1").build();

        when(playerRepository.findByName("player1")).thenReturn(Optional.of(player));

        GlobalException ex = assertThrows(GlobalException.class, () -> playerService.createPlayer(createPlayerRequestBodyDTO));

        assertThat(ex.getError()).isInstanceOf(PlayerAlreadyExistsError.class);

        verify(playerRepository, times(1)).findByName("player1");
    }

    @Test
    public void createPlayer() {
        CreatePlayerRequestBodyDTO createPlayerRequestBodyDTO =
                CreatePlayerRequestBodyDTO.builder().name("player1").build();

        Player player = Player.builder().id(1L).name("player1").build();
        GetPlayerResponseDTO playerDTO = GetPlayerResponseDTO.builder().id(1L).name("player1").build();

        when(playerRepository.findByName("player1")).thenReturn(Optional.empty());
        when(playerMapper.createPlayerRequestBodyDTOToEntity(createPlayerRequestBodyDTO)).thenReturn(player);
        when(playerRepository.save(player)).thenReturn(player);
        when(playerMapper.playerEntityToGetTournamentDTO(player)).thenReturn(playerDTO);

        GetPlayerResponseDTO actualPlayer = playerService.createPlayer(createPlayerRequestBodyDTO);

        assertThat(actualPlayer).isEqualTo(playerDTO);

        verify(playerRepository, times(1)).findByName("player1");
        verify(playerMapper, times(1)).createPlayerRequestBodyDTOToEntity(any());
        verify(playerRepository, times(1)).save(player);
        verify(playerMapper, times(1)).playerEntityToGetTournamentDTO(any());
        //verify(playerValidator, times(1)).validateCreatePlayerRequestBodyDTO(createPlayerRequestBodyDTO);
    }

    @Test
    public void updatePlayer_playerNotFound_throwException() {

        UpdatePlayerRequestBodyDTO updatePlayerRequestBodyDTO =
                UpdatePlayerRequestBodyDTO.builder().name("player1").build();

        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException ex = assertThrows(GlobalException.class, () -> playerService.updatePlayer(1L, updatePlayerRequestBodyDTO));

        assertThat(ex.getError()).isInstanceOf(PlayerNotFoundError.class);

        verify(playerRepository, times(1)).findById(1L);
    }

    @Test
    public void updatePlayer() {
        UpdatePlayerRequestBodyDTO updatePlayerRequestBodyDTO =
                UpdatePlayerRequestBodyDTO.builder().name("player2").build();

        Player player = Player.builder().id(1L).name("player1").build();
        GetPlayerResponseDTO playerDTO = GetPlayerResponseDTO.builder().id(1L).name("player2").build();

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(playerMapper.playerEntityToGetTournamentDTO(player)).thenReturn(playerDTO);

        GetPlayerResponseDTO actualPlayer = playerService.updatePlayer(1L, updatePlayerRequestBodyDTO);

        assertThat(actualPlayer).isEqualTo(playerDTO);

        verify(playerRepository, times(1)).findById(1L);
        verify(playerMapper, times(1)).updatePlayerFromDTO(player, updatePlayerRequestBodyDTO);
        verify(playerMapper, times(1)).playerEntityToGetTournamentDTO(any());
    }

    @Test
    public void deletePlayer() {

        Player player = Player.builder().id(1L).name("player1").build();
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        playerService.deletePlayer(1L);

        verify(playerRepository, times(1)).findById(1L);
        verify(playerRepository, times(1)).deleteById(1L);

    }

    @Test
    public void deletePlayer_playerNotFound_throwException() {

        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException ex = assertThrows(GlobalException.class, () -> playerService.deletePlayer(1L));

        assertThat(ex.getError()).isInstanceOf(PlayerNotFoundError.class);

        verify(playerRepository, times(1)).findById(1L);
    }
}
