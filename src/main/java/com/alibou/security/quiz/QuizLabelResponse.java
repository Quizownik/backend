package com.alibou.security.quiz;

import com.alibou.security.question.QuestionResponse;

import java.util.List;

public record QuizLabelResponse(
        Integer id,
        int position,
        String name,
        String category,
        String level,
        boolean isMastered,
        Integer numberOfQuestions
) {}
