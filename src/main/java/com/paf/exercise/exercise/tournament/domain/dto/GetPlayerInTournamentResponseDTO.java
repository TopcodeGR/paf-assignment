package com.paf.exercise.exercise.tournament.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPlayerInTournamentResponseDTO {
    private Long id;
    private String name;
}
