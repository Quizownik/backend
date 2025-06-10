package com.alibou.security.user;

import com.alibou.security.result.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/")
@RequiredArgsConstructor
@Tag(name = "Użytkownicy", description = "API do zarządzania kontem użytkownika i statystykami")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService service;

    @Operation(
            summary = "Zmiana hasła użytkownika",
            description = "Endpoint pozwalający zalogowanemu użytkownikowi na zmianę swojego hasła"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hasło zostało zmienione pomyślnie"),
            @ApiResponse(responseCode = "401", description = "Podano nieprawidłowe aktualne hasło")
    })
    @PatchMapping
    public ResponseEntity<?> changePassword(
            @Parameter(description = "Dane potrzebne do zmiany hasła")
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        try {
            service.changePassword(request, connectedUser);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Pobieranie statystyk użytkownika",
            description = "Endpoint pozwalający użytkownikowi na pobranie swoich statystyk z quizów"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zwrócono statystyki użytkownika")
    })
    @GetMapping("/me")
    public ResponseEntity<UserStatsResponse> getUserStats(Principal connectedUser) {
        return ResponseEntity.ok(service.getUserStats(connectedUser));
    }

    @Operation(
            summary = "Ranking użytkowników",
            description = "Endpoint pozwalający na pobranie listy najlepszych użytkowników według punktacji"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zwrócono listę użytkowników z rankingu")
    })
    @GetMapping("/topRankedUsers")
    public ResponseEntity<List<UserRankingResponse>> getTopRankedUsers() {
        return ResponseEntity.ok(service.getTopRankedUsers());
    }
}
