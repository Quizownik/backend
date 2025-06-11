package com.alibou.security.quiz;

import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="api/v1/quizzes")
@Tag(name = "Quizy", description = "API do zarządzania quizami w systemie")
@SecurityRequirement(name = "bearerAuth")
public class QuizController {
    private final QuizService quizService;

    @Operation(
            summary = "Pobieranie wszystkich quizów",
            description = "Endpoint pozwalający na pobranie listy wszystkich dostępnych quizów"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zwrócono listę wszystkich quizów")
    })
    @GetMapping
    public ResponseEntity<List<QuizResponse>> findAll() {return ResponseEntity.ok(quizService.findAll());}

    @Operation(
            summary = "Pobieranie quizów według kategorii",
            description = "Endpoint pozwalający na pobranie quizów należących do określonej kategorii z paginacją"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zwrócono listę quizów dla wybranej kategorii")
    })
    @GetMapping("/sorted")
    public ResponseEntity<Page<QuizLabelResponse>> getLabelQuizzes(
            @Parameter(description = "Kategoria quizów do pobrania")
            @RequestParam(required = true) Category category,
            @PageableDefault(size = 10, sort = "name") Pageable pageable,
            Principal connectedUser) {

        Page<QuizLabelResponse> result = quizService.getLabelQuizzes(category, pageable, connectedUser);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Pobieranie quizów według kategorii i poziomu trudności",
            description = "Endpoint pozwalający na pobranie quizów należących do określonej kategorii i poziomu trudności z paginacją"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zwrócono listę quizów dla wybranej kategorii i poziomu")
    })
    @GetMapping("/sorted2params")
    public ResponseEntity<Page<QuizLabelResponse>> getLabelQuizzes2params(
            @Parameter(description = "Kategoria quizów do pobrania")
            @RequestParam(required = true) Category category,
            @Parameter(description = "Poziom trudności quizów do pobrania")
            @RequestParam(required = true) Level level,
            @PageableDefault(size = 10, sort = "name") Pageable pageable,
            Principal connectedUser) {

        Page<QuizLabelResponse> result = quizService.getLabelQuizzes2(category,level, pageable, connectedUser);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Pobieranie szczegółów quizu",
            description = "Endpoint pozwalający na pobranie szczegółowych informacji o konkretnym quizie"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zwrócono szczegóły quizu"),
            @ApiResponse(responseCode = "404", description = "Quiz o podanym ID nie został znaleziony")
    })
    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuiz(
            @PathVariable Integer id,
            Principal connectedUser) {

        QuizResponse result = quizService.getQuiz(id, connectedUser);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Tworzenie nowego quizu",
            description = "Endpoint pozwalający administratorowi na utworzenie nowego quizu z pytaniami"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Quiz utworzony pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do wykonania operacji")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createQuiz(@RequestBody QuizRequest request) {
        String resultString = quizService.createQuizWithQuestions(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultString);
    }

    @Operation(
            summary = "Usunięcie quizu",
            description = "Endpoint pozwalający administratorowi na usunięcie quizu"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Quiz usunięty pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do wykonania operacji"),
            @ApiResponse(responseCode = "404", description = "Quiz o podanym ID nie został znaleziony")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Integer id) {
        quizService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Aktualizacja quizu",
            description = "Endpoint pozwalający administratorowi na aktualizację istniejącego quizu"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz zaktualizowany pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do wykonania operacji"),
            @ApiResponse(responseCode = "404", description = "Quiz o podanym ID nie został znaleziony")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<QuizResponse> updateQuiz(
            @PathVariable Integer id,
            @RequestBody QuizRequest request) {
        QuizResponse updated = quizService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Generowanie quizu",
            description = "Endpoint pozwalający administratorowi na automatyczne wygenerowanie quizu na podstawie parametrów"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz wygenerowany pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do wykonania operacji")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/generate")
    public ResponseEntity<QuizResponse> generateQuiz(@RequestBody QuizGenerateRequest request) {

        QuizResponse generatedQuiz = quizService.generateQuiz(request);
        return ResponseEntity.ok(generatedQuiz);
    }
}
