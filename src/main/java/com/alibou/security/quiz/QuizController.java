package com.alibou.security.quiz;

import com.alibou.security.question.Question;
import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="api/v1/quizes")
public class QuizController {
    private final QuizService quizService;
    private final RequestService requestService;

    //    @Autowired
//    public QuizController(QuizService quizService) {
//        this.quizService = quizService;
//    }
    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody QuizRequest_DTO request //poprawic dto dla pytan i odpowiedzi tak zeby bylo mniej wprowadzania
    ){
        quizService.save(request);
        return ResponseEntity.accepted().build();
    }


    @GetMapping
    public ResponseEntity<List<Quiz>> findAll() {return ResponseEntity.ok(quizService.findAll());}
}
