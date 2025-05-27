package com.alibou.security.question;

import com.alibou.security.quiz.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody QuestionRequest request) {
        QuestionResponse response = questionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Integer id,
            @RequestBody QuestionRequest request) {
        QuestionResponse response = questionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/category/{category}")
    public ResponseEntity<Page<QuestionResponse>> getQuestionsByCategory(
            @PathVariable Category category,
            @PageableDefault(size = 10, sort = "question") Pageable pageable) {
        Page<QuestionResponse> page = questionService.findAllByCategory(category, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/getAllQuestions")
    public ResponseEntity<Page<QuestionResponse>> getAllQuestions(
            @PageableDefault(size = 10, sort = "question") Pageable pageable) {
        Page<QuestionResponse> page = questionService.findAll(pageable);
        return ResponseEntity.ok(page);
    }


}