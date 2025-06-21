package com.alibou.security.result;

import java.time.LocalDateTime;
import java.util.List;

public record ResultResponse(
        Long quizId,
        Integer userId,
        String quizName,
        LocalDateTime finishedAt,
        Long duration,
        List<Integer> questionOrder,
        List<Integer> chosenAnswers,
        Integer correct,
        Integer fails
) {
}
