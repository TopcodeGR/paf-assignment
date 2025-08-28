package com.paf.exercise.exercise.tournament.service;


import com.paf.exercise.exercise.player.domain.Player;
import com.paf.exercise.exercise.player.service.PlayerService;
import com.paf.exercise.exercise.tournament.domain.Tournament;
import com.paf.exercise.exercise.tournament.domain.dto.*;
import com.paf.exercise.exercise.tournament.mapper.TournamentMapper;
import com.paf.exercise.exercise.tournament.repository.TournamentRepository;
import com.paf.exercise.exercise.tournament.validation.TournamentValidator;
import com.paf.exercise.exercise.web.exception.GlobalException;
import com.paf.exercise.exercise.web.exception.error.PlayerIsAlreadyInTournamentError;
import com.paf.exercise.exercise.web.exception.error.PlayerIsNotInTournamentError;
import com.paf.exercise.exercise.web.exception.error.TournamentAlreadyExistsError;
import com.paf.exercise.exercise.web.exception.error.TournamentNotFoundError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;
    private final TournamentValidator tournamentValidator;
    private final PlayerService playerService;

    public List<GetTournamentResponseDTO> getTournaments() {
        return tournamentRepository.findAll().stream().map(tournamentMapper::tournamentEntityToGetTournamentDTO).collect(Collectors.toList());
    }

    public GetTournamentResponseDTO getTournament(Long id) {
        return tournamentRepository.findById(id)
                .map(tournamentMapper::tournamentEntityToGetTournamentDTO)
                .orElseThrow(()->new GlobalException(new TournamentNotFoundError()));
    }

    @Transactional
    public GetTournamentResponseDTO createTournament(CreateTournamentRequestBodyDTO dto) {

        Optional<Tournament> tournament = tournamentRepository.findByName(dto.getName());

        if (tournament.isPresent()) {
            throw new GlobalException(new TournamentAlreadyExistsError());
        }

        tournamentValidator.validateCreateTournamentRequestBodyDTO(dto);

        Tournament createdTournament = tournamentRepository.save(tournamentMapper.createTournamentRequestBodyDTOToEntity(dto));

        return tournamentMapper.tournamentEntityToGetTournamentDTO(createdTournament);
    }

    @Transactional
    public GetTournamentResponseDTO updateTournament(Long id, UpdateTournamentRequestBodyDTO dto) {

        Tournament tournament = tournamentRepository
                .findById(id)
                .orElseThrow(()->new GlobalException(new TournamentNotFoundError()));

        tournamentValidator.validateUpdateTournamentRequestBodyDTO(dto);

        tournamentMapper.updateTournamentFromDTO(tournament, dto);


        return tournamentMapper.tournamentEntityToGetTournamentDTO(tournament);
    }

    @Transactional
    public void deleteTournament(Long id) {

        tournamentRepository
                .findById(id)
                .orElseThrow(()->new GlobalException(new TournamentNotFoundError()));


        tournamentRepository.deleteById(id);
    }

    public GetPlayersInTournamentResponseDTO getPlayersInTournament(Long id) {

        Tournament tournament = tournamentRepository
                .findById(id)
                .orElseThrow(()->new GlobalException(new TournamentNotFoundError()));

        List<GetPlayerInTournamentResponseDTO> players =
                tournament.getPlayers().stream().map(tournamentMapper::playerEntityToGetPlayerInTournamentResponseDTO).collect(Collectors.toList());

        return GetPlayersInTournamentResponseDTO
                .builder()
                .tournamentId(id)
                .players(players)
                .build();
    }

    @Transactional
    public GetPlayersInTournamentResponseDTO addPlayerInTournament(Long tournamentId, AddPlayerIntoTournamentRequestBodyDTO dto) {

        Tournament tournament = tournamentRepository
                .findById(tournamentId)
                .orElseThrow(()->new GlobalException(new TournamentNotFoundError()));

        Player player = playerService.getPlayerEntity(dto.getPlayerId());

        if (tournament.getPlayers().contains(player)) {
            throw new GlobalException(new PlayerIsAlreadyInTournamentError());
        }

        tournament.getPlayers().add(player);


        List<GetPlayerInTournamentResponseDTO> players =
                tournament.getPlayers().stream().map(tournamentMapper::playerEntityToGetPlayerInTournamentResponseDTO).collect(Collectors.toList());

        return GetPlayersInTournamentResponseDTO
                .builder()
                .tournamentId(tournamentId)
                .players(players)
                .build();
    }


    @Transactional
    public GetPlayersInTournamentResponseDTO removePlayerFromTournament(Long tournamentId, Long playerId) {
        Tournament tournament = tournamentRepository
                .findById(tournamentId)
                .orElseThrow(()->new GlobalException(new TournamentNotFoundError()));

        Player player = playerService.getPlayerEntity(playerId);

        if (!tournament.getPlayers().contains(player)) {
            throw new GlobalException(new PlayerIsNotInTournamentError());
        }
        tournament.getPlayers().remove(player);

        List<GetPlayerInTournamentResponseDTO> players =
                tournament.getPlayers().stream().map(tournamentMapper::playerEntityToGetPlayerInTournamentResponseDTO).collect(Collectors.toList());

        return GetPlayersInTournamentResponseDTO
                .builder()
                .tournamentId(tournamentId)
                .players(players)
                .build();
    }
}
