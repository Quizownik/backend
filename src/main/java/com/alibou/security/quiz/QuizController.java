package com.alibou.security.quiz;

import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.core.service.RequestService;
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
    public ResponseEntity<List<Quiz>> findAll() {return ResponseEntity.ok(quizService.findAll());}



    @PostMapping("/create")
    public ResponseEntity<String> createQuiz(@RequestBody AddQuestionsToQuizRequest request) {
        quizService.createQuizWithQuestions(request.position(), request.questionIds());
        return ResponseEntity.status(HttpStatus.CREATED).body("Quiz created successfully.");
    }
}
