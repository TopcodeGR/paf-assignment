package com.paf.exercise.exercise.player.domain.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPlayerResponseDTO {
    private Long id;
    private String name;
}
