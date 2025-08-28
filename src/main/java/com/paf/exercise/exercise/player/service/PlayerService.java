package com.paf.exercise.exercise.player.service;

import com.paf.exercise.exercise.player.domain.Player;
import com.paf.exercise.exercise.player.domain.dto.CreatePlayerRequestBodyDTO;
import com.paf.exercise.exercise.player.domain.dto.GetPlayerResponseDTO;
import com.paf.exercise.exercise.player.domain.dto.UpdatePlayerRequestBodyDTO;
import com.paf.exercise.exercise.player.mapper.PlayerMapper;
import com.paf.exercise.exercise.player.repository.PlayerRepository;
import com.paf.exercise.exercise.player.validation.PlayerValidator;
import com.paf.exercise.exercise.web.exception.GlobalException;
import com.paf.exercise.exercise.web.exception.error.PlayerAlreadyExistsError;
import com.paf.exercise.exercise.web.exception.error.PlayerNotFoundError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final PlayerValidator validator;
    private final PlayerValidator playerValidator;

    public List<GetPlayerResponseDTO> getPlayers() {
        return playerRepository.findAll().stream().map(playerMapper::playerEntityToGetTournamentDTO).collect(Collectors.toList());
    }

    public GetPlayerResponseDTO getPlayer(Long id) {
        return playerRepository.findById(id)
                .map(playerMapper::playerEntityToGetTournamentDTO)
                .orElseThrow(()->new GlobalException(new PlayerNotFoundError()));
    }

    public Player getPlayerEntity(Long id) {
        return playerRepository.findById(id).orElseThrow(()->new GlobalException(new PlayerNotFoundError()));
    }

    @Transactional
    public GetPlayerResponseDTO createPlayer(CreatePlayerRequestBodyDTO dto) {

        Optional<Player> player = playerRepository.findByName(dto.getName());

        if (player.isPresent()) {
            throw new GlobalException(new PlayerAlreadyExistsError());
        }

        validator.validateCreatePlayerRequestBodyDTO(dto);

        Player createdPlayer = playerRepository.save(playerMapper.createPlayerRequestBodyDTOToEntity(dto));

        return playerMapper.playerEntityToGetTournamentDTO(createdPlayer);
    }

    @Transactional
    public GetPlayerResponseDTO updatePlayer(Long id, UpdatePlayerRequestBodyDTO dto) {

        Player player = playerRepository
                .findById(id)
                .orElseThrow(()->new GlobalException(new PlayerNotFoundError()));

        playerValidator.validateUpdatePlayerRequestBodyDTO(dto);

        playerMapper.updatePlayerFromDTO(player, dto);

        return playerMapper.playerEntityToGetTournamentDTO(player);
    }

    @Transactional
    public void deletePlayer(Long id) {

        playerRepository
                .findById(id)
                .orElseThrow(()->new GlobalException(new PlayerNotFoundError()));


        playerRepository.deleteById(id);
    }
}
