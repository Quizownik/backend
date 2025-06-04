package com.alibou.security.quiz;

import lombok.Builder;

import java.util.List;
@Builder
public record QuizRequest(
        String name,
        Category category, //ingoruj dla edycji quizu
        List<Integer> questionIds
) {
}
