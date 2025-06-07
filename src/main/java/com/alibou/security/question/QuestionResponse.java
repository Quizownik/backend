package com.alibou.security.question;

import com.alibou.security.answer.AnswerResponse;
import com.alibou.security.quiz.Category;
import com.alibou.security.quiz.Level;

import java.util.List;

public record QuestionResponse(
        Integer id,
        String question,
        Level level,
        Category category,
        List<AnswerResponse>answers) {
}
