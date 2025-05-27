package com.alibou.security.question;

import com.alibou.security.answer.AnswerRequest;
import com.alibou.security.quiz.Category;
import lombok.Builder;

import java.util.List;
@Builder
public record QuestionRequest(
       // Integer id,
        String question,
        Category category,
        List<AnswerRequest>answers) {
}
