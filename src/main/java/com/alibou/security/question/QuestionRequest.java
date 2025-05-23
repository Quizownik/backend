package com.alibou.security.question;

import com.alibou.security.answer.AnswerRequest;
import com.alibou.security.quiz.Category;

import java.util.List;

public record QuestionRequest(
       // Integer id,
        String question,
        Category category,
        List<AnswerRequest>answers) {
}
