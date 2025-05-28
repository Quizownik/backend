package com.alibou.security.quiz;

import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="api/v1/quizes")
public class QuizController {
    private final QuizService quizService;
   //    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<QuizResponse>> findAll() {return ResponseEntity.ok(quizService.findAll());}

    @GetMapping("/sorted") //tutaj jest zle z DTO dla question
    public ResponseEntity<Page<QuizResponse>> getQuizzes(
            @RequestParam(required = false) Category category,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        Page<QuizResponse> result = quizService.getQuizzes(category, pageable);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/create")
    public ResponseEntity<String> createQuiz(@RequestBody QuizRequest request) {
        quizService.createQuizWithQuestions(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Quiz created successfully.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Integer id) {
        quizService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizResponse> updateQuiz(
            @PathVariable Integer id,
            @RequestBody QuizRequest request) {
        QuizResponse updated = quizService.update(id, request);
        return ResponseEntity.ok(updated);
    }
}
