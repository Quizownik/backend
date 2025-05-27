package com.alibou.security.user;

import java.time.LocalDateTime;

public record UserStatsResponse(
        String firstName,
        String lastName,
        String username,
        String email,
        Role role,
        LocalDateTime createdDate,
        Integer numOfDoneQuizzes,
        Integer numOfOnlyFullyCorrectQuizzes
) {}
