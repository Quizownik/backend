package com.alibou.security.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository repository;

    public void save(AnswerRequest request) {

        var answer = Answer.builder()
                .answer(request.answer())
                .isCorrect(request.isCorrect())
                .build();

        repository.save(answer);
    }

    public List<Answer> findAll() {
        return repository.findAll();
    }
}
