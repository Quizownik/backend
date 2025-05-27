package com.alibou.security.result;

import com.alibou.security.quiz.QuizRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record ResultResponse(
        Integer quizId,
        Integer userId,
        LocalDateTime finishedAt,
        Long duration,
        List<Integer> questionOrder,
        List<Integer> chosenAnswers,
        Integer correct,
        Integer fails
//        QuizRequest quiz,
//        LocalDateTime finishedAt,
//        Duration duration,
//        Integer userId,
//        List<Integer> questionIdOrder,
//        List <Integer> chosenAnswerId

) {
}
