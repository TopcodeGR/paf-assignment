package com.paf.exercise.exercise.tournament.domain.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPlayersInTournamentResponseDTO {

    private Long tournamentId;
    private List<GetPlayerInTournamentResponseDTO> players;
}
