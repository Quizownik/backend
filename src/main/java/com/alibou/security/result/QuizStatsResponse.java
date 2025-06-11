package com.alibou.security.result;

public record QuizStatsResponse(
        Long quizId,
        String quizTitle,
        //String category,
        //String level,
        double medianScore,
        long totalSolutions,
        long last7Days,
        long days7to14,
        long days14to21
) {

}
