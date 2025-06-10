package com.alibou.security.result;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
@Tag(name = "Wyniki", description = "API do zarządzania wynikami quizów")
@SecurityRequirement(name = "bearerAuth")
public class ResultController {

    private final ResultService resultService;

    @Operation(
            summary = "Zapisanie wyniku quizu",
            description = "Endpoint pozwalający na zapisanie wyniku ukończonego quizu"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Wynik zapisany pomyślnie"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wyniku")
    })
    @PostMapping
    public ResponseEntity<String> saveResult(@RequestBody ResultRequest request) {
        resultService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Result created successfully.");
    }

    @Operation(
            summary = "Pobieranie wszystkich wyników",
            description = "Endpoint pozwalający na pobranie wszystkich wyników quizów z paginacją"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zwrócono listę wszystkich wyników")
    })
    @GetMapping
    public ResponseEntity<Page<ResultResponse>> getAllResults(
            @Parameter(description = "Parametry paginacji i sortowania")
            @PageableDefault(size = 10, sort = "finishedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(resultService.getAll(pageable));
    }

    @Operation(
            summary = "Pobieranie wyników zalogowanego użytkownika",
            description = "Endpoint pozwalający na pobranie wszystkich wyników quizów bieżącego użytkownika"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zwrócono listę wyników użytkownika")
    })
    @GetMapping("/my")
    public ResponseEntity<Page<ResultResponse>> getMyResults(
            @Parameter(description = "Parametry paginacji i sortowania")
            @PageableDefault(size = 10, sort = "finishedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResultResponse> results = resultService.getResultsForCurrentUser(pageable);
        return ResponseEntity.ok(results);
    }
}

