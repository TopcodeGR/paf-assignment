package com.paf.exercise.exercise.player.validation;


import com.paf.exercise.exercise.player.domain.dto.CreatePlayerRequestBodyDTO;
import com.paf.exercise.exercise.player.domain.dto.UpdatePlayerRequestBodyDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
public class PlayerValidator {

    public void validateCreatePlayerRequestBodyDTO(@Valid CreatePlayerRequestBodyDTO dto){}

    public void validateUpdatePlayerRequestBodyDTO(@Valid UpdatePlayerRequestBodyDTO dto){}
}
