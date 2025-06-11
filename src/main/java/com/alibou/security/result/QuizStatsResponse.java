package com.alibou.security.result;

import java.util.Map;

public record QuizStatsResponse(
        Long quizId,
        String name,
        String category,
        String level,
        double medianScore,
        long totalSolutions,
//        long last7Days,
//        long days7to14,
//        long days14to21,
        Map<Integer, Long> solvedPerDayAgo
) {

}
