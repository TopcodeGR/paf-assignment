package com.paf.exercise.exercise.tournament.domain.dto;

import com.paf.exercise.exercise.tournament.domain.TournamentRewardCurrency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTournamentResponseDTO {

    private Long id;
    private String name;
    private Integer rewardAmount;
    private TournamentRewardCurrency rewardCurrency;
}
