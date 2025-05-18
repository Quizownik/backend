package com.alibou.security.question;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository repository;

    public void save(QuestionRequest request) {
        var question = Question.builder()
                .id(request.getId())
                .question(request.getQuestion())
                .build();

        repository.save(question);
    }

    public List<Question> findAll() {return repository.findAll();}
}
