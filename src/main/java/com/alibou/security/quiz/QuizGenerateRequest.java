package com.alibou.security.quiz;

public record QuizGenerateRequest(
        String name,
        Category category,
        Level level,
        Integer numberOfQuestions
) {
}