package com.alibou.security.question;

import com.alibou.security.quiz.Category;
import lombok.RequiredArgsConstructor;
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


@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Pytania", description = "API do zarządzania pytaniami w systemie")
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(
            summary = "Tworzenie nowego pytania",
            description = "Endpoint pozwalający administratorowi na utworzenie nowego pytania"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pytanie utworzone pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do wykonania operacji")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody QuestionRequest request) {
        QuestionResponse response = questionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Aktualizacja pytania",
            description = "Endpoint pozwalający administratorowi na aktualizację istniejącego pytania"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pytanie zaktualizowane pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do wykonania operacji"),
            @ApiResponse(responseCode = "404", description = "Pytanie o podanym ID nie zostało znalezione")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Integer id,
            @RequestBody QuestionRequest request) {
        QuestionResponse response = questionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Usunięcie pytania",
            description = "Endpoint pozwalający administratorowi na usunięcie pytania"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pytanie usunięte pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do wykonania operacji"),
            @ApiResponse(responseCode = "404", description = "Pytanie o podanym ID nie zostało znalezione")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Pobieranie pytań według kategorii",
            description = "Endpoint pozwalający administratorowi na pobieranie pytań według wybranej kategorii"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zwrócono listę pytań"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do wykonania operacji")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<QuestionResponse>> getQuestionsByCategory(
            @PathVariable Category category,
            @PageableDefault(size = 10, sort = "question") Pageable pageable) {
        Page<QuestionResponse> page = questionService.findAllByCategory(category, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Pobieranie wszystkich pytań",
            description = "Endpoint pozwalający administratorowi na pobieranie wszystkich pytań z systemu"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zwrócono listę wszystkich pytań"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do wykonania operacji")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllQuestions")
    public ResponseEntity<Page<QuestionResponse>> getAllQuestions(
            @PageableDefault(size = 10, sort = "question") Pageable pageable) {
        Page<QuestionResponse> page = questionService.findAll(pageable);
        return ResponseEntity.ok(page);
    }


}