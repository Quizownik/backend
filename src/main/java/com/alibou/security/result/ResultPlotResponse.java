package com.alibou.security.result;

import java.util.Map;

public record ResultPlotResponse(
//        Long quizId,
//        String name,
        String category,
        double averageScore,
        Map<Integer, Long> solvedPerDayAgo

) {
}
