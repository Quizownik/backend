package com.alibou.security.answer;

public record AnswerRequest(
        String answer,
        boolean isCorrect) {
}
