package com.alibou.security.question;

import com.alibou.security.answer.AnswerResponse;
import com.alibou.security.quiz.Category;

import java.util.List;

public record QuestionResponse(
        Integer id,
        String question,
        Category category,
        List<AnswerResponse>answers) {
}
