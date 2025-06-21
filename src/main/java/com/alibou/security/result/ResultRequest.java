package com.alibou.security.result;

import com.alibou.security.quiz.QuizRequest;
import com.alibou.security.quiz.QuizResponse;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record ResultRequest(
        Integer quizId,
        Integer userId,
        LocalDateTime finishedAt,
        Long duration,
        List<Integer> questionOrder,
        List<Integer> chosenAnswers
        ) {
}
