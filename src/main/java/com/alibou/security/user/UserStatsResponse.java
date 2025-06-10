package com.alibou.security.user;

import com.alibou.security.quiz.Level;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDateTime;

public record UserStatsResponse(
        String firstName,
        String lastName,
        String username,
        String email,
        Role role,
        Level level,
        LocalDateTime createdDate,
        Integer numOfDoneQuizzes,
        Integer numOfOnlyFullyCorrectQuizzes,
        Integer score,
        Integer id
) {}
