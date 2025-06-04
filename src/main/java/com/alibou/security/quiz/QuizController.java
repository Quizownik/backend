package com.alibou.security.quiz;

import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="api/v1/quizzes")
public class QuizController {
    private final QuizService quizService;


    @GetMapping
    public ResponseEntity<List<QuizResponse>> findAll() {return ResponseEntity.ok(quizService.findAll());}

    @GetMapping("/sorted")
    public ResponseEntity<Page<QuizLabelResponse>> getLabelQuizzes(
            @RequestParam(required = true) Category category,
            @PageableDefault(size = 10, sort = "name") Pageable pageable,
            Principal connectedUser) {

        Page<QuizLabelResponse> result = quizService.getLabelQuizzes(category, pageable, connectedUser);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sorted2params")
    public ResponseEntity<Page<QuizLabelResponse>> getLabelQuizzes2params(
            @RequestParam(required = true) Category category,
            @RequestParam(required = true) Level level,
            @PageableDefault(size = 10, sort = "name") Pageable pageable,
            Principal connectedUser) {

        Page<QuizLabelResponse> result = quizService.getLabelQuizzes2(category,level, pageable, connectedUser);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuiz(@PathVariable Integer id) {

        QuizResponse result = quizService.getQuiz(id);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/create")
    public ResponseEntity<String> createQuiz(@RequestBody QuizRequest request) {
        String resultString = quizService.createQuizWithQuestions(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultString);
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
