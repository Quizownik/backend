package com.alibou.security.quiz;

import java.util.List;

public record QuizRequest(
        int position,
        String name,
        Category category, //ingoruj dla edycji quizu
        List<Integer> questionIds
) {
}
