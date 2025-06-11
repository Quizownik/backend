package com.alibou.security.result;

public record ResultPlotResponse(
        String category,
        double averageScore,
        long last7Days,
        long days7to14,
        long days14to21

) {
}
