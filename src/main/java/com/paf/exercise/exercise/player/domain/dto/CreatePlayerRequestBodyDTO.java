package com.paf.exercise.exercise.player.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePlayerRequestBodyDTO {

    @NotNull
    @NotBlank
    private String name;
}
