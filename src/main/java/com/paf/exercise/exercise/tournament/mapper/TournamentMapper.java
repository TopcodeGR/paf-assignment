package com.paf.exercise.exercise.tournament.mapper;


import com.paf.exercise.exercise.player.domain.Player;
import com.paf.exercise.exercise.tournament.domain.dto.CreateTournamentRequestBodyDTO;
import com.paf.exercise.exercise.tournament.domain.dto.GetPlayerInTournamentResponseDTO;
import com.paf.exercise.exercise.tournament.domain.dto.GetTournamentResponseDTO;
import com.paf.exercise.exercise.tournament.domain.Tournament;
import com.paf.exercise.exercise.tournament.domain.dto.UpdateTournamentRequestBodyDTO;
import org.springframework.stereotype.Service;

@Service
public class TournamentMapper {

    public GetTournamentResponseDTO tournamentEntityToGetTournamentDTO(Tournament tournament) {

        return GetTournamentResponseDTO
                .builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .rewardAmount(tournament.getRewardAmount())
                .rewardCurrency(tournament.getRewardCurrency())
                .build();
    }

    public Tournament createTournamentRequestBodyDTOToEntity(CreateTournamentRequestBodyDTO dto) {

        return Tournament
                .builder()
                .name(dto.getName())
                .rewardAmount(dto.getRewardAmount())
                .rewardCurrency(dto.getRewardCurrency())
                .build();
    }

    public void updateTournamentFromDTO(Tournament tournament, UpdateTournamentRequestBodyDTO dto) {

        if (dto == null ) {
            return;
        }

        if (dto.getName() != null ) {
            tournament.setName(dto.getName() );
        }
        if (dto.getRewardAmount() != null ) {
            tournament.setRewardAmount( dto.getRewardAmount() );
        }
        if ( dto.getRewardCurrency() != null ) {
            tournament.setRewardCurrency(dto.getRewardCurrency() );
        }
    }

    public GetPlayerInTournamentResponseDTO playerEntityToGetPlayerInTournamentResponseDTO(Player player) {

        return GetPlayerInTournamentResponseDTO
                .builder()
                .id(player.getId())
                .name(player.getName())
                .build();
    }
}
