package com.alibou.security.quiz;

import lombok.Builder;

import java.util.List;

@Builder
public record QuizRequest2(
        String name,
        Category category, //ingoruj dla edycji quizu
        List<Integer> questionIds,
        Level level
) {
}
