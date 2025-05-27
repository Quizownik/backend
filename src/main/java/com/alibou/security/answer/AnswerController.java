package com.alibou.security.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
public class AnswerController {

//    private final AnswerService service;
//
//    @PostMapping
//    public ResponseEntity<?> save(
//            @RequestBody AnswerRequest request
//    ){
//        service.save(request);
//        return ResponseEntity.accepted().build();
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Answer>> findAll(){return ResponseEntity.ok(service.findAll());}
}
