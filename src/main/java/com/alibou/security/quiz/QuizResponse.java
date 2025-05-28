package com.alibou.security.quiz;

import com.alibou.security.question.QuestionResponse;

import java.util.List;

public record QuizResponse(
        Integer id,
        int position,
        String name,
        String category,
        List<QuestionResponse> questions,
        String level,
        boolean isMastered
) {}
