package com.paf.exercise.exercise.tournament.mapper;

import com.paf.exercise.exercise.player.domain.Player;
import com.paf.exercise.exercise.tournament.domain.Tournament;
import com.paf.exercise.exercise.tournament.domain.TournamentRewardCurrency;
import com.paf.exercise.exercise.tournament.domain.dto.CreateTournamentRequestBodyDTO;
import com.paf.exercise.exercise.tournament.domain.dto.GetPlayerInTournamentResponseDTO;
import com.paf.exercise.exercise.tournament.domain.dto.GetTournamentResponseDTO;
import com.paf.exercise.exercise.tournament.domain.dto.UpdateTournamentRequestBodyDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class TournamentMapperTest {

    @InjectMocks
    private TournamentMapper mapper;

    @Test
    public void tournamentEntityToGetTournamentDTO() {
        Tournament tournament = Tournament
                .builder()
                .id(1L)
                .name("tournament1")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.EUR)
                .build();
        GetTournamentResponseDTO expectedDTO = GetTournamentResponseDTO
                        .builder()
                        .id(1L)
                        .name("tournament1")
                        .rewardAmount(1000)
                        .rewardCurrency(TournamentRewardCurrency.EUR)
                        .build();

        GetTournamentResponseDTO actualDTO = mapper.tournamentEntityToGetTournamentDTO(tournament);

        assertThat(actualDTO.getId()).isEqualTo(expectedDTO.getId());
        assertThat(actualDTO.getName()).isEqualTo(expectedDTO.getName());
        assertThat(actualDTO.getRewardAmount()).isEqualTo(expectedDTO.getRewardAmount());
        assertThat(actualDTO.getRewardCurrency()).isEqualTo(expectedDTO.getRewardCurrency());
    }

    @Test
    public void createTournamentRequestBodyDTOToEntity() {
        CreateTournamentRequestBodyDTO dto = CreateTournamentRequestBodyDTO
                .builder()
                .name("tournament")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.EUR)
                .build();

        Tournament expectedTournament = Tournament
                .builder()
                .name("tournament")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.EUR)
                .build();

        Tournament actualTournament = mapper.createTournamentRequestBodyDTOToEntity(dto);

        assertThat(actualTournament.getName()).isEqualTo(expectedTournament.getName());
        assertThat(actualTournament.getRewardAmount()).isEqualTo(expectedTournament.getRewardAmount());
        assertThat(actualTournament.getRewardCurrency()).isEqualTo(expectedTournament.getRewardCurrency());
    }

    @Test
    public void updateTournamentFromDTO() {
        UpdateTournamentRequestBodyDTO dto = UpdateTournamentRequestBodyDTO
                .builder()
                .name("tournament1")
                .rewardAmount(1500)
                .rewardCurrency(TournamentRewardCurrency.EUR)
                .build();

        Tournament tournament = Tournament
                .builder()
                .name("tournament")
                .rewardAmount(1000)
                .rewardCurrency(TournamentRewardCurrency.USD)
                .build();

        mapper.updateTournamentFromDTO(tournament, dto);

        assertThat(tournament.getName()).isEqualTo(dto.getName());
        assertThat(tournament.getRewardCurrency()).isEqualTo(dto.getRewardCurrency());
        assertThat(tournament.getRewardAmount()).isEqualTo(dto.getRewardAmount());
    }

    @Test
    public void playerEntityToGetPlayerInTournamentResponseDTO() {
        Player player = Player.builder().id(1L).name("player1").build();

        GetPlayerInTournamentResponseDTO expectedDTO = GetPlayerInTournamentResponseDTO.builder().name("player1").id(1L).build();

        GetPlayerInTournamentResponseDTO actualDTO = mapper.playerEntityToGetPlayerInTournamentResponseDTO(player);

        assertThat(actualDTO.getId()).isEqualTo(expectedDTO.getId());
        assertThat(actualDTO.getName()).isEqualTo(expectedDTO.getName());
    }
}
