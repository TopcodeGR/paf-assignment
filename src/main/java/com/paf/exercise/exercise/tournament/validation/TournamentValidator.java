package com.paf.exercise.exercise.tournament.validation;


import com.paf.exercise.exercise.tournament.domain.dto.CreateTournamentRequestBodyDTO;
import com.paf.exercise.exercise.tournament.domain.dto.UpdateTournamentRequestBodyDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
public class TournamentValidator {

    public void validateUpdateTournamentRequestBodyDTO(@Valid UpdateTournamentRequestBodyDTO dto){}

    public void validateCreateTournamentRequestBodyDTO(@Valid CreateTournamentRequestBodyDTO dto){}
}
