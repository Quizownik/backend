package com.alibou.security.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
