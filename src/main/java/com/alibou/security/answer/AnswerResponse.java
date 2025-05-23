package com.alibou.security.answer;

public record AnswerResponse(
    Integer id,
    String answer,
    boolean isCorrect) {

}
