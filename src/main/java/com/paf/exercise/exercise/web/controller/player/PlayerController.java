package com.paf.exercise.exercise.web.controller.player;


import com.paf.exercise.exercise.authorization.annotation.RequiredAuthorities;
import com.paf.exercise.exercise.authorization.domain.AuthorityCode;
import com.paf.exercise.exercise.player.domain.dto.CreatePlayerRequestBodyDTO;
import com.paf.exercise.exercise.player.domain.dto.GetPlayerResponseDTO;
import com.paf.exercise.exercise.player.domain.dto.UpdatePlayerRequestBodyDTO;
import com.paf.exercise.exercise.player.service.PlayerService;
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
@RequestMapping("/api/v1.0/players")
@RequiredArgsConstructor
@Tag(name = "Players")
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    @Operation(summary = "Get all players")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = GetPlayerResponseDTO.class)))})
    @ApiResponse(responseCode="401", description = "Unauthorized", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.GET_PLAYERS})
    public ResponseEntity<List<GetPlayerResponseDTO>> getPlayers() {
        return ResponseEntity.ok().body(playerService.getPlayers());
    }

    @PostMapping
    @Operation(summary = "Create a player")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetPlayerResponseDTO.class))})
    @ApiResponse(responseCode="400", description = "Bad request", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="409", description = "Player already exists", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.CREATE_PLAYER})
    public ResponseEntity<GetPlayerResponseDTO> createPlayer(@RequestBody CreatePlayerRequestBodyDTO dto) {
        return ResponseEntity.ok().body(playerService.createPlayer(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a player")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetPlayerResponseDTO.class))})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="404", description = "Player not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.GET_PLAYER})
    public ResponseEntity<GetPlayerResponseDTO> getPlayer(@PathVariable Long id) {
        return ResponseEntity.ok().body(playerService.getPlayer(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a player")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetPlayerResponseDTO.class))})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="404", description = "Player not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.UPDATE_PLAYER})
    public ResponseEntity<GetPlayerResponseDTO> updatePlayer(@PathVariable Long id, @RequestBody UpdatePlayerRequestBodyDTO dto) {
        return ResponseEntity.ok().body(playerService.updatePlayer(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a player")
    @ApiResponse(responseCode="200", description = "OK", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())})
    @ApiResponse(responseCode="403", description = "Forbidden", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="404", description = "Player not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode="500", description = "Internal server error", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @RequiredAuthorities(authorities = {AuthorityCode.DELETE_PLAYER})
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.ok().build();
    }
}
