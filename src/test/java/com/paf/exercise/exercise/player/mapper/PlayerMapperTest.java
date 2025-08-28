package com.paf.exercise.exercise.player.mapper;


import com.paf.exercise.exercise.player.domain.Player;
import com.paf.exercise.exercise.player.domain.dto.CreatePlayerRequestBodyDTO;
import com.paf.exercise.exercise.player.domain.dto.GetPlayerResponseDTO;
import com.paf.exercise.exercise.player.domain.dto.UpdatePlayerRequestBodyDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(MockitoExtension.class)
public class PlayerMapperTest {

    @InjectMocks
    private PlayerMapper playerMapper;

    @Test
    public void playerEntityToGetTournamentDTO() {
        Player player = Player.builder().id(1L).name("player1").build();
        GetPlayerResponseDTO expectedPlayerDTO = GetPlayerResponseDTO.builder().id(1L).name("player1").build();

        GetPlayerResponseDTO actualPlayerDTO = playerMapper.playerEntityToGetTournamentDTO(player);

        assertThat(actualPlayerDTO.getId()).isEqualTo(expectedPlayerDTO.getId());
        assertThat(actualPlayerDTO.getName()).isEqualTo(expectedPlayerDTO.getName());
    }

    @Test
    public void createPlayerRequestBodyDTOToEntity() {
        CreatePlayerRequestBodyDTO dto = CreatePlayerRequestBodyDTO.builder().name("player").build();

        Player expectedPlayer = Player.builder().id(1L).name("player").build();

        Player actualPlayer = playerMapper.createPlayerRequestBodyDTOToEntity(dto);

        assertThat(actualPlayer.getName()).isEqualTo(expectedPlayer.getName());
    }

    @Test
    public void updatePlayerFromDTO() {
        UpdatePlayerRequestBodyDTO dto = UpdatePlayerRequestBodyDTO.builder().name("player1").build();
        Player player = Player.builder().id(1L).name("player").build();

        playerMapper.updatePlayerFromDTO(player, dto);

        assertThat(player.getName()).isEqualTo(dto.getName());
    }

}
