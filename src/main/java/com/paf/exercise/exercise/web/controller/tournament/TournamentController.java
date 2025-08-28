package com.paf.exercise.exercise.web.controller.tournament;

import com.paf.exercise.exercise.authorization.annotation.RequiredAuthorities;
import com.paf.exercise.exercise.authorization.domain.AuthorityCode;
import com.paf.exercise.exercise.tournament.domain.dto.*;
import com.paf.exercise.exercise.tournament.service.TournamentService;
import com.paf.exercise.exercise.web.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1.0/tournaments")
@RequiredArgsConstructor
@Tag(name = "Tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    @Operation(summary = "Get all tournaments")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = GetTournamentResponseDTO.class)))})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.GET_TOURNAMENTS})
    public ResponseEntity<List<GetTournamentResponseDTO>> getTournaments() {
        return ResponseEntity.ok().body(tournamentService.getTournaments());
    }

    @PostMapping
    @Operation(summary = "Create a tournament")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetTournamentResponseDTO.class))})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="409", description = "Tournament already exists", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.CREATE_TOURNAMENT})
    public ResponseEntity<GetTournamentResponseDTO> createTournament(@RequestBody CreateTournamentRequestBodyDTO dto) {
        return ResponseEntity.ok().body(tournamentService.createTournament(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a tournament")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetTournamentResponseDTO.class))})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="404", description = "Tournament not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.CREATE_TOURNAMENT})
    public ResponseEntity<GetTournamentResponseDTO> getTournament(@PathVariable Long id) {
        return ResponseEntity.ok().body(tournamentService.getTournament(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a tournament")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetTournamentResponseDTO.class))})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="404", description = "Tournament not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.UPDATE_TOURNAMENT})
    public ResponseEntity<GetTournamentResponseDTO> updateTournament(@PathVariable Long id, @RequestBody UpdateTournamentRequestBodyDTO dto) {
        return ResponseEntity.ok().body(tournamentService.updateTournament(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a tournament")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="404", description = "Tournament not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.DELETE_TOURNAMENT})
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/players")
    @Operation(summary = "Get players in a tournament")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetPlayersInTournamentResponseDTO.class))})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="404", description = "Tournament not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.GET_PLAYERS_IN_TOURNAMENT})
    public ResponseEntity<GetPlayersInTournamentResponseDTO> getPlayersInTournament(@PathVariable Long id) {
        return ResponseEntity.ok().body(tournamentService.getPlayersInTournament(id));
    }

    @PostMapping("/{id}/players")
    @Operation(summary = "Add a player into a tournament")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetPlayersInTournamentResponseDTO.class))})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="404", description = "Player not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="409", description = "Player already in tournament", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.ADD_PLAYER_INTO_TOURNAMENT})
    public ResponseEntity<GetPlayersInTournamentResponseDTO> addPlayerIntoTournament(@PathVariable Long id, @RequestBody AddPlayerIntoTournamentRequestBodyDTO dto) {
        return ResponseEntity.ok().body(tournamentService.addPlayerInTournament(id, dto));
    }

    @DeleteMapping("/{tournamentId}/players/{playerId}")
    @Operation(summary = "Remove a player from a tournament")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetPlayersInTournamentResponseDTO.class))})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="404", description = "Player not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="404", description = "Player is not in tournament", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.REMOVE_PLAYER_FROM_TOURNAMENT})
    public ResponseEntity<GetPlayersInTournamentResponseDTO> removePlayerFromTournament(@PathVariable Long tournamentId, @PathVariable Long playerId) {
        return ResponseEntity.ok().body(tournamentService.removePlayerFromTournament(tournamentId, playerId));
    }

}
