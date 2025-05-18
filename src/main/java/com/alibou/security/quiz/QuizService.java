package com.alibou.security.quiz;

import com.alibou.security.question.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository repository;

    public void save(QuizRequest_DTO request) {
        var quiz = Quiz.builder()
//                .id(request.getId())
//                .createdBy(request.getCreatedBy())
                .position(request.getPosition())
                .questions(request.getQuestions())
                .build();
        repository.save(quiz);
    }

    public List<Quiz> findAll() {
        return repository.findAll();
    }

}

