package com.alibou.security.answer;

import com.alibou.security.answer.Answer;
import com.alibou.security.answer.AnswerRepository;
import com.alibou.security.answer.AnswerRequest;
import com.alibou.security.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository repository;

    public void save(AnswerRequest request) {
        var answer = Answer.builder()
                .answer(request.getAnswer())
                .isCorrect(request.isCorrect())
                //.question(request.que jak to polaczyc?
                .build();
        repository.save(answer);
    }

    public List<Answer> findAll() {
        return repository.findAll();
    }
}
