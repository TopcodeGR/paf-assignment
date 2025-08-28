package com.paf.exercise.exercise.tournament.domain.dto;


import com.paf.exercise.exercise.tournament.domain.TournamentRewardCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class UpdateTournamentRequestBodyDTO {

    private String name;

    @Positive
    private Integer rewardAmount;

    private TournamentRewardCurrency rewardCurrency;
}
