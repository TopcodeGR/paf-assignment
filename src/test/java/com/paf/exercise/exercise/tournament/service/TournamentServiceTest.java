package com.paf.exercise.exercise.tournament.service;


import com.paf.exercise.exercise.player.domain.Player;
import com.paf.exercise.exercise.player.service.PlayerService;
import com.paf.exercise.exercise.tournament.domain.Tournament;
import com.paf.exercise.exercise.tournament.domain.TournamentRewardCurrency;
import com.paf.exercise.exercise.tournament.domain.dto.*;
import com.paf.exercise.exercise.tournament.mapper.TournamentMapper;
import com.paf.exercise.exercise.tournament.repository.TournamentRepository;
import com.paf.exercise.exercise.tournament.validation.TournamentValidator;
import com.paf.exercise.exercise.web.exception.GlobalException;
import com.paf.exercise.exercise.web.exception.error.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @InjectMocks
    private TournamentService tournamentService;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TournamentMapper tournamentMapper;

    @Mock
    private TournamentValidator tournamentValidator;

    @Mock
    private PlayerService playerService;

    @Test
    public void getTournaments() {

        Tournament tournament1 = Tournament
                .builder()
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();


        Tournament tournament2 = Tournament
                .builder()
                .name("tournament2")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        GetTournamentResponseDTO tournament1ResponseDTO = GetTournamentResponseDTO
                .builder()
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        GetTournamentResponseDTO tournament2ResponseDTO = GetTournamentResponseDTO
                .builder()
                .name("tournament2")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        when(tournamentRepository.findAll()).thenReturn(List.of(tournament1,tournament2));
        when(tournamentMapper.tournamentEntityToGetTournamentDTO(tournament1)).thenReturn(tournament1ResponseDTO);
        when(tournamentMapper.tournamentEntityToGetTournamentDTO(tournament2)).thenReturn(tournament2ResponseDTO);

        List<GetTournamentResponseDTO> actualTournaments = tournamentService.getTournaments();

        assertThat(actualTournaments.size()).isEqualTo(2);
        assertThat(actualTournaments).containsExactlyElementsOf(List.of(tournament1ResponseDTO, tournament2ResponseDTO));

        verify(tournamentRepository, times(1)).findAll();
        verify(tournamentMapper, times(2)).tournamentEntityToGetTournamentDTO(any());
    }

    @Test
    public void getTournament_tournamentDoesNotExist_throwException() {

        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException ex = assertThrows(GlobalException.class, () -> tournamentService.getTournament(1L));

        assertThat(ex.getError()).isInstanceOf(TournamentNotFoundError.class);

        verify(tournamentRepository, times(1)).findById(1L);
    }


    @Test
    public void getTournament() {

        Tournament tournament = Tournament
                .builder()
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();


        GetTournamentResponseDTO tournamentResponseDTO = GetTournamentResponseDTO
                .builder()
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();


        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentMapper.tournamentEntityToGetTournamentDTO(tournament)).thenReturn(tournamentResponseDTO);

        GetTournamentResponseDTO actualTournament = tournamentService.getTournament(1L);

        assertThat(actualTournament).isEqualTo(tournamentResponseDTO);

        verify(tournamentRepository, times(1)).findById(1L);
        verify(tournamentMapper, times(1)).tournamentEntityToGetTournamentDTO(any());
    }

    @Test
    public void createTournament_TournamentAlreadyExists_throwException() {

        CreateTournamentRequestBodyDTO dto = CreateTournamentRequestBodyDTO
                        .builder()
                        .name("tournament1")
                        .rewardAmount(1000)
                        .rewardCurrency(TournamentRewardCurrency.USD)
                        .build();

        Tournament tournament = Tournament
                .builder()
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        when(tournamentRepository.findByName("tournament1")).thenReturn(Optional.of(tournament));

        GlobalException ex = assertThrows(GlobalException.class, () -> tournamentService.createTournament(dto));

        assertThat(ex.getError()).isInstanceOf(TournamentAlreadyExistsError.class);

        verify(tournamentRepository, times(1)).findByName("tournament1");
    }

    @Test
    public void createTournament() {
        CreateTournamentRequestBodyDTO dto = CreateTournamentRequestBodyDTO
                .builder()
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        Tournament tournament = Tournament
                .builder()
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        GetTournamentResponseDTO tournamentResponseDTO = GetTournamentResponseDTO
                .builder()
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        when(tournamentRepository.findByName("tournament1")).thenReturn(Optional.empty());
        when(tournamentMapper.createTournamentRequestBodyDTOToEntity(dto)).thenReturn(tournament);
        when(tournamentRepository.save(tournament)).thenReturn(tournament);
        when(tournamentMapper.tournamentEntityToGetTournamentDTO(tournament)).thenReturn(tournamentResponseDTO);

        GetTournamentResponseDTO actualTournament = tournamentService.createTournament(dto);

        assertThat(actualTournament).isEqualTo(tournamentResponseDTO);

        verify(tournamentRepository, times(1)).findByName("tournament1");
        verify(tournamentMapper, times(1)).createTournamentRequestBodyDTOToEntity(any());
        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentMapper, times(1)).tournamentEntityToGetTournamentDTO(any());
        verify(tournamentValidator, times(1)).validateCreateTournamentRequestBodyDTO(dto);
    }

    @Test
    public void updateTournament_tournamentNotFound_throwException() {

        UpdateTournamentRequestBodyDTO dto = UpdateTournamentRequestBodyDTO
                        .builder()
                        .name("tournament1")
                        .rewardAmount(1000)
                        .rewardCurrency(TournamentRewardCurrency.USD)
                        .build();

        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException ex = assertThrows(GlobalException.class, () -> tournamentService.updateTournament(1L, dto));

        assertThat(ex.getError()).isInstanceOf(TournamentNotFoundError.class);

        verify(tournamentRepository, times(1)).findById(1L);
    }


    @Test
    public void updateTournament() {
        UpdateTournamentRequestBodyDTO dto = UpdateTournamentRequestBodyDTO
                .builder()
                .name("tournament2")
                .rewardAmount(1500)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        Tournament tournament = Tournament
                .builder()
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        GetTournamentResponseDTO tournamentResponseDTO = GetTournamentResponseDTO
                .builder()
                .name("tournament2")
                .rewardAmount(1500)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentMapper.tournamentEntityToGetTournamentDTO(tournament)).thenReturn(tournamentResponseDTO);

        GetTournamentResponseDTO actualTournament = tournamentService.updateTournament(1L, dto);

        assertThat(actualTournament).isEqualTo(tournamentResponseDTO);

        verify(tournamentRepository, times(1)).findById(1L);
        verify(tournamentMapper, times(1)).updateTournamentFromDTO(tournament, dto);
        verify(tournamentMapper, times(1)).tournamentEntityToGetTournamentDTO(any());
    }

    @Test
    public void deleteTournament() {

        Tournament tournament = Tournament
                .builder()
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        tournamentService.deleteTournament(1L);

        verify(tournamentRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1)).deleteById(1L);

    }

    @Test
    public void deleteTournament_tournamentNotFound_throwException() {

        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException ex = assertThrows(GlobalException.class, () -> tournamentService.deleteTournament(1L));

        assertThat(ex.getError()).isInstanceOf(TournamentNotFoundError.class);

        verify(tournamentRepository, times(1)).findById(1L);
    }

    @Test
    public void getPlayersInTournament_tournamentNotFound_throwException() {

        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException ex = assertThrows(GlobalException.class, () -> tournamentService.getPlayersInTournament(1L));

        assertThat(ex.getError()).isInstanceOf(TournamentNotFoundError.class);

        verify(tournamentRepository, times(1)).findById(1L);
    }

    @Test
    public void getPlayersInTournament() {

        Player player1 = Player.builder().id(1L).name("player1").build();
        Player player2 = Player.builder().id(2L).name("player2").build();

        GetPlayerInTournamentResponseDTO dto1 = GetPlayerInTournamentResponseDTO.builder().id(1L).name("player1").build();
        GetPlayerInTournamentResponseDTO dto2 = GetPlayerInTournamentResponseDTO.builder().id(2L).name("player2").build();

        Tournament tournament = Tournament
                .builder()
                .id(1L)
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .players(Set.of(player1,player2))
                .build();

        List<GetPlayerInTournamentResponseDTO> expectedPlayers = List.of(dto1, dto2);

        GetPlayersInTournamentResponseDTO expectedResponse = GetPlayersInTournamentResponseDTO.builder().tournamentId(1L).players(expectedPlayers).build();

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentMapper.playerEntityToGetPlayerInTournamentResponseDTO(player1)).thenReturn(dto1);
        when(tournamentMapper.playerEntityToGetPlayerInTournamentResponseDTO(player2)).thenReturn(dto2);

        GetPlayersInTournamentResponseDTO actualResponse =  tournamentService.getPlayersInTournament(1L);

        assertThat(actualResponse.getTournamentId()).isEqualTo(expectedResponse.getTournamentId());
        assertThat(actualResponse.getPlayers()).containsExactlyInAnyOrderElementsOf(expectedResponse.getPlayers());

        verify(tournamentRepository, times(1)).findById(1L);
        verify(tournamentMapper, times(2)).playerEntityToGetPlayerInTournamentResponseDTO(any());
    }

    @Test
    public void addPlayerInTournament_tournamentNotFound_throwException() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException ex = assertThrows(GlobalException.class, () -> tournamentService.addPlayerInTournament(1L,AddPlayerIntoTournamentRequestBodyDTO.builder().build()));

        assertThat(ex.getError()).isInstanceOf(TournamentNotFoundError.class);

        verify(tournamentRepository, times(1)).findById(1L);
    }

    @Test
    public void addPlayerInTournament_playerNotFound_throwException() {

        Tournament tournament = Tournament
                .builder()
                .id(1L)
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        AddPlayerIntoTournamentRequestBodyDTO dto = AddPlayerIntoTournamentRequestBodyDTO.builder().playerId(1L).build();

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getPlayerEntity(1L)).thenThrow(new GlobalException(new PlayerNotFoundError()));

        GlobalException ex = assertThrows(GlobalException.class, () -> tournamentService.addPlayerInTournament(1L, dto));

        assertThat(ex.getError()).isInstanceOf(PlayerNotFoundError.class);

        verify(tournamentRepository, times(1)).findById(1L);
        verify(playerService, times(1)).getPlayerEntity(1L);
    }

    @Test
    public void addPlayerInTournament_playerIsAlreadyInTournament_throwException() {

        Player player = Player.builder().id(1L).name("player1").build();

        Tournament tournament = Tournament
                .builder()
                .id(1L)
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .players(Set.of(player))
                .build();

        AddPlayerIntoTournamentRequestBodyDTO dto = AddPlayerIntoTournamentRequestBodyDTO.builder().playerId(1L).build();

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getPlayerEntity(1L)).thenReturn(player);


        GlobalException ex = assertThrows(GlobalException.class, () -> tournamentService.addPlayerInTournament(1L, dto));

        assertThat(ex.getError()).isInstanceOf(PlayerIsAlreadyInTournamentError.class);

        verify(tournamentRepository, times(1)).findById(1L);
        verify(playerService, times(1)).getPlayerEntity(1L);
    }

    @Test
    public void addPlayerInTournament() {

        Player player = Player.builder().id(1L).name("player1").build();
        GetPlayerInTournamentResponseDTO playerDto = GetPlayerInTournamentResponseDTO.builder().id(1L).name("player1").build();

        Tournament tournament = Tournament
                .builder()
                .id(1L)
                .name("tournament1")
                .rewardAmount(1000)
                .players(new HashSet<>())
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        AddPlayerIntoTournamentRequestBodyDTO dto = AddPlayerIntoTournamentRequestBodyDTO.builder().playerId(1L).build();


        GetPlayersInTournamentResponseDTO expectedResponse = GetPlayersInTournamentResponseDTO.builder().tournamentId(1L).players(List.of(playerDto)).build();

        when(tournamentMapper.playerEntityToGetPlayerInTournamentResponseDTO(player)).thenReturn(playerDto);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getPlayerEntity(1L)).thenReturn(player);


        GetPlayersInTournamentResponseDTO actualResponse = tournamentService.addPlayerInTournament(1L, dto);

        assertThat(actualResponse.getTournamentId()).isEqualTo(expectedResponse.getTournamentId());
        assertThat(actualResponse.getPlayers()).containsExactlyInAnyOrderElementsOf(expectedResponse.getPlayers());

        verify(tournamentRepository, times(1)).findById(1L);
        verify(playerService, times(1)).getPlayerEntity(1L);
    }

    @Test
    public void removePlayerFromTournament_tournamentNotFound_throwException() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException ex = assertThrows(GlobalException.class, () -> tournamentService.removePlayerFromTournament(1L,1L));

        assertThat(ex.getError()).isInstanceOf(TournamentNotFoundError.class);

        verify(tournamentRepository, times(1)).findById(1L);
    }

    @Test
    public void removePlayerFromTournament_playerNotFound_throwException() {

        Tournament tournament = Tournament
                .builder()
                .id(1L)
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();


        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getPlayerEntity(1L)).thenThrow(new GlobalException(new PlayerNotFoundError()));

        GlobalException ex = assertThrows(GlobalException.class, () -> tournamentService.removePlayerFromTournament(1L, 1L));

        assertThat(ex.getError()).isInstanceOf(PlayerNotFoundError.class);

        verify(tournamentRepository, times(1)).findById(1L);
        verify(playerService, times(1)).getPlayerEntity(1L);
    }

    @Test
    public void removePlayerFromTournament_playerIsNotInTournament_throwException() {

        Player player = Player.builder().id(1L).name("player1").build();

        Tournament tournament = Tournament
                .builder()
                .id(1L)
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .players(Set.of())
                .build();

        AddPlayerIntoTournamentRequestBodyDTO dto = AddPlayerIntoTournamentRequestBodyDTO.builder().playerId(1L).build();

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getPlayerEntity(1L)).thenReturn(player);


        GlobalException ex = assertThrows(GlobalException.class, () -> tournamentService.removePlayerFromTournament(1L, 1L));

        assertThat(ex.getError()).isInstanceOf(PlayerIsNotInTournamentError.class);

        verify(tournamentRepository, times(1)).findById(1L);
        verify(playerService, times(1)).getPlayerEntity(1L);
    }

    @Test
    public void removePlayerFromTournament() {

        Player player1 = Player.builder().id(1L).name("player1").build();
        Player player2 = Player.builder().id(2L).name("player2").build();

        GetPlayerInTournamentResponseDTO playerDto2 = GetPlayerInTournamentResponseDTO.builder().id(2L).name("player2").build();

        Set<Player> players = new HashSet<>();
        players.add(player1);
        players.add(player2);

        Tournament tournament = Tournament
                .builder()
                .id(1L)
                .name("tournament1")
                .rewardAmount(1000)
                .players(players)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        GetPlayersInTournamentResponseDTO expectedResponse = GetPlayersInTournamentResponseDTO.builder().tournamentId(1L).players(List.of(playerDto2)).build();

        when(tournamentMapper.playerEntityToGetPlayerInTournamentResponseDTO(player2)).thenReturn(playerDto2);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getPlayerEntity(1L)).thenReturn(player1);

        GetPlayersInTournamentResponseDTO actualResponse = tournamentService.removePlayerFromTournament(1L, 1L);

        assertThat(actualResponse.getTournamentId()).isEqualTo(expectedResponse.getTournamentId());
        assertThat(actualResponse.getPlayers()).containsExactlyInAnyOrderElementsOf(expectedResponse.getPlayers());

        verify(tournamentRepository, times(1)).findById(1L);
        verify(playerService, times(1)).getPlayerEntity(1L);
    }

}
