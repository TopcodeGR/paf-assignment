package com.paf.exercise.exercise.player.mapper;


import com.paf.exercise.exercise.player.domain.Player;
import com.paf.exercise.exercise.player.domain.dto.CreatePlayerRequestBodyDTO;
import com.paf.exercise.exercise.player.domain.dto.GetPlayerResponseDTO;
import com.paf.exercise.exercise.player.domain.dto.UpdatePlayerRequestBodyDTO;
import org.springframework.stereotype.Service;

@Service
public class PlayerMapper {

    public GetPlayerResponseDTO playerEntityToGetTournamentDTO(Player player) {
        return GetPlayerResponseDTO
                .builder()
                .id(player.getId())
                .name(player.getName())
                .build();
    }

    public Player createPlayerRequestBodyDTOToEntity(CreatePlayerRequestBodyDTO dto) {

        return Player
                .builder()
                .name(dto.getName())
                .build();
    }

    public void updatePlayerFromDTO(Player player, UpdatePlayerRequestBodyDTO dto) {

        if (dto == null) {
            return;
        }

        if (dto.getName() != null ) {
            player.setName(dto.getName());
        }
    }
}
