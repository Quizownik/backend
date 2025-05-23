package com.alibou.security.question;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // POST - tworzenie nowego pytania z odpowiedziami
    @PostMapping
    public ResponseEntity<String> createQuestion(@RequestBody QuestionRequest request) {
        questionService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Question saved successfully.");
    }

    // GET - pojedyncze pytanie z odpowiedziami
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Integer id) {
        return questionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET - lista wszystkich pytań z odpowiedziami
    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        List<QuestionResponse> responses = questionService.findAll();
        return ResponseEntity.ok(responses);
    }

    // (opcjonalnie) GET - pytania przypisane do konkretnego quizu
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByQuiz(@PathVariable Integer quizId) {
        // TODO: Dodaj logikę filtrowania po quizId w serwisie
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
